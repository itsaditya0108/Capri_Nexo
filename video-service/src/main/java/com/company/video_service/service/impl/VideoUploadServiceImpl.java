package com.company.video_service.service.impl;

import com.company.video_service.dto.VideoChunkUploadResponse;
import com.company.video_service.dto.VideoFinalizeJobResponse;
import com.company.video_service.dto.VideoUploadInitRequest;
import com.company.video_service.dto.VideoUploadInitResponse;
import com.company.video_service.dto.VideoUploadStatusResponse;
import com.company.video_service.entity.UploadSessionStatus;
import com.company.video_service.entity.VideoJobType;
import com.company.video_service.entity.VideoProcessingJob;
import com.company.video_service.entity.VideoProcessingJobStatus;
import com.company.video_service.entity.VideoUploadChunk;
import com.company.video_service.entity.VideoUploadSession;
import com.company.video_service.repository.VideoProcessingJobRepository;
import com.company.video_service.repository.VideoRepository;
import com.company.video_service.repository.VideoUploadChunkRepository;
import com.company.video_service.repository.VideoUploadSessionRepository;
import com.company.video_service.service.VideoUploadService;
import com.company.video_service.util.HashUtil;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VideoUploadServiceImpl implements VideoUploadService {

    private final VideoUploadSessionRepository sessionRepository;
    private final VideoUploadChunkRepository chunkRepository;
    private final VideoRepository videoRepository;
    private final VideoProcessingJobRepository processingJobRepository;

    @Value("${video.upload.checksum.enabled:true}")
    private boolean checksumEnabled;

    @Value("${video.storage.temp-path}")
    private String tempStoragePath;

    public VideoUploadServiceImpl(VideoUploadSessionRepository sessionRepository,
            VideoUploadChunkRepository chunkRepository, VideoRepository videoRepository,
            VideoProcessingJobRepository processingJobRepository) {
        this.sessionRepository = sessionRepository;
        this.chunkRepository = chunkRepository;
        this.videoRepository = videoRepository;
        this.processingJobRepository = processingJobRepository;
    }

    @Override
    public VideoUploadInitResponse initUpload(Long userId, VideoUploadInitRequest request) {

        // validations
        if (request.getFileSize() <= 0) {
            throw new RuntimeException("INVALID_FILE_SIZE");
        }

        // example max size = 500MB
        long maxSize = 500L * 1024 * 1024;
        if (request.getFileSize() > maxSize) {
            throw new RuntimeException("FILE_TOO_LARGE");
        }

        // chunk size decision (fixed for now)
        int chunkSize = 5 * 1024 * 1024;

        int totalChunks = (int) Math.ceil((double) request.getFileSize() / (double) chunkSize);

        // 1. Check if video already exists in final gallery
        if (videoRepository.existsByUserIdAndTitleAndOriginalFileSizeAndIsDeletedFalse(userId, request.getFileName(),
                request.getFileSize())) {
            // We can return a specific status or just a new exception that frontend handles
            throw new RuntimeException("VIDEO_ALREADY_EXISTS");
        }

        // 2. Check if there is an active session (not expired, and not
        // failed/completed)
        var existingSession = sessionRepository
                .findFirstByUserIdAndOriginalFileNameAndOriginalFileSizeAndStatusInOrderByCreatedTimestampDesc(
                        userId, request.getFileName(), request.getFileSize(),
                        java.util.List.of(UploadSessionStatus.INITIATED, UploadSessionStatus.UPLOADING,
                                UploadSessionStatus.MERGING));

        if (existingSession.isPresent()) {
            VideoUploadSession s = existingSession.get();
            // If it's not expired and not in a terminal state that we want to avoid, return
            // it
            if (LocalDateTime.now().isBefore(s.getExpiresTimestamp())) {
                // If it was merging but something failed, we might want to allow re-finalize or
                // resume?
                // For now, let's just return it.
                return new VideoUploadInitResponse(
                        s.getUploadUid(),
                        s.getChunkSizeBytes(),
                        s.getTotalChunks(),
                        s.getExpiresTimestamp(),
                        s.getStatus().name());
            }
        }

        String uploadUid = UUID.randomUUID().toString();

        LocalDateTime expires = LocalDateTime.now().plusHours(24);

        VideoUploadSession session = new VideoUploadSession();
        session.setUploadUid(uploadUid);
        session.setUserId(userId);
        session.setOriginalFileName(request.getFileName());
        session.setOriginalFileSize(request.getFileSize());
        session.setMimeType(request.getMimeType());
        session.setDurationSeconds(request.getDurationSeconds());
        session.setVideoWidth(request.getWidth());
        session.setVideoHeight(request.getHeight());
        session.setChunkSizeBytes(chunkSize);
        session.setTotalChunks(totalChunks);
        session.setUploadedChunksCount(0);
        session.setStatus(UploadSessionStatus.INITIATED);
        session.setExpiresTimestamp(expires);

        sessionRepository.save(session);

        return new VideoUploadInitResponse(
                uploadUid,
                chunkSize,
                totalChunks,
                expires,
                session.getStatus().name());
    }

    @Override
    public VideoChunkUploadResponse uploadChunk(Long userId,
            String uploadUid,
            Integer chunkIndex,
            byte[] chunkBytes,
            String sha256) {

        VideoUploadSession session = sessionRepository.findByUploadUid(uploadUid)
                .orElseThrow(() -> new RuntimeException("UPLOAD_SESSION_NOT_FOUND"));

        // ownership check
        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("FORBIDDEN");
        }

        // expiry check
        if (LocalDateTime.now().isAfter(session.getExpiresTimestamp())) {
            throw new RuntimeException("UPLOAD_SESSION_EXPIRED");
        }

        // status check
        if (session.getStatus() == UploadSessionStatus.COMPLETED) {
            throw new RuntimeException("UPLOAD_ALREADY_COMPLETED");
        }

        // chunk index validation
        if (chunkIndex < 0 || chunkIndex >= session.getTotalChunks()) {
            throw new RuntimeException("INVALID_CHUNK_INDEX");
        }

        // chunk size validation
        if (chunkBytes.length > session.getChunkSizeBytes()) {
            throw new RuntimeException("CHUNK_TOO_LARGE");
        }

        String computedHash = HashUtil.sha256Hex(chunkBytes);
        if (checksumEnabled) {

            if (sha256 == null || sha256.isBlank()) {
                throw new RuntimeException("MISSING_SHA256_HEADER");
            }

            if (!computedHash.equalsIgnoreCase(sha256)) {
                throw new RuntimeException("CHUNK_CHECKSUM_MISMATCH");
            }
        }

        // idempotent check
        var existingChunk = chunkRepository.findByUploadUidAndChunkIndex(uploadUid, chunkIndex);
        if (existingChunk.isPresent()) {
            return new VideoChunkUploadResponse(uploadUid, chunkIndex,
                    existingChunk.get().getChunkSizeBytes(),
                    "CHUNK_ALREADY_EXISTS");
        }

        // store chunk file
        String folderPath = tempStoragePath + "/" + uploadUid;
        java.io.File folder = new java.io.File(folderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filePath = folderPath + "/" + chunkIndex + ".part";

        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath)) {
            fos.write(chunkBytes);
            System.out.println(
                    "DEBUG: Chunk " + chunkIndex + " written to " + filePath + " (" + chunkBytes.length + " bytes)");
        } catch (Exception e) {
            throw new RuntimeException("CHUNK_WRITE_FAILED");
        }

        // save chunk in DB
        VideoUploadChunk chunk = new VideoUploadChunk();
        chunk.setUploadUid(uploadUid);
        chunk.setChunkIndex(chunkIndex);
        chunk.setChunkFilePath(filePath);
        chunk.setChunkSizeBytes((long) chunkBytes.length);
        chunk.setSha256Checksum(computedHash);

        chunkRepository.save(chunk);

        // update session status + uploaded count
        if (session.getStatus() == UploadSessionStatus.INITIATED) {
            session.setStatus(UploadSessionStatus.UPLOADING);
        }

        session.setUploadedChunksCount(session.getUploadedChunksCount() + 1);
        sessionRepository.save(session);

        return new VideoChunkUploadResponse(uploadUid, chunkIndex,
                (long) chunkBytes.length,
                "CHUNK_UPLOADED");
    }

    @Override
    public VideoUploadStatusResponse getUploadStatus(Long userId, String uploadUid) {

        VideoUploadSession session = sessionRepository.findByUploadUid(uploadUid)
                .orElseThrow(() -> new RuntimeException("UPLOAD_SESSION_NOT_FOUND"));

        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("FORBIDDEN");
        }

        var chunks = chunkRepository.findByUploadUidOrderByChunkIndexAsc(uploadUid);

        java.util.List<Integer> uploadedChunkIndexes = new java.util.ArrayList<>();
        for (var chunk : chunks) {
            uploadedChunkIndexes.add(chunk.getChunkIndex());
        }

        VideoUploadStatusResponse response = new VideoUploadStatusResponse();
        response.setUploadUid(uploadUid);
        response.setFileName(session.getOriginalFileName());
        response.setFileSize(session.getOriginalFileSize());
        response.setChunkSizeBytes(session.getChunkSizeBytes());
        response.setTotalChunks(session.getTotalChunks());

        response.setUploadedChunks(uploadedChunkIndexes);
        response.setUploadedCount(uploadedChunkIndexes.size());
        response.setRemainingChunks(session.getTotalChunks() - uploadedChunkIndexes.size());

        response.setStatus(session.getStatus().name());
        response.setExpiresTimestamp(session.getExpiresTimestamp());

        return response;
    }

    @Override
    public VideoFinalizeJobResponse finalizeUpload(Long userId, String uploadUid) {

        VideoUploadSession session = sessionRepository.findByUploadUid(uploadUid)
                .orElseThrow(() -> new RuntimeException("UPLOAD_SESSION_NOT_FOUND"));

        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("FORBIDDEN");
        }

        if (LocalDateTime.now().isAfter(session.getExpiresTimestamp())) {
            throw new RuntimeException("UPLOAD_SESSION_EXPIRED");
        }

        long uploadedCount = chunkRepository.countByUploadUid(uploadUid);

        if (uploadedCount != session.getTotalChunks()) {
            throw new RuntimeException("ALL_CHUNKS_NOT_UPLOADED");
        }

        if (session.getStatus() == UploadSessionStatus.MERGING) {
            return new VideoFinalizeJobResponse(uploadUid, "MERGING_ALREADY_STARTED", session.getMergeJobUid());
        }

        if (session.getStatus() == UploadSessionStatus.COMPLETED) {
            return new VideoFinalizeJobResponse(uploadUid, "UPLOAD_ALREADY_COMPLETED", session.getMergeJobUid());
        }

        session.setStatus(UploadSessionStatus.MERGING);

        String mergeJobUid = java.util.UUID.randomUUID().toString();
        session.setMergeJobUid(mergeJobUid);

        sessionRepository.save(session);

        VideoProcessingJob job = new VideoProcessingJob(uploadUid);
        job.setJobUid(mergeJobUid);
        job.setUploadUid(uploadUid);
        job.setUserId(userId);
        job.setStatus(VideoProcessingJobStatus.PENDING);
        job.setJobType(VideoJobType.MERGE_UPLOAD);

        processingJobRepository.save(job);

        return new VideoFinalizeJobResponse(uploadUid, "MERGING_STARTED", mergeJobUid);
    }

    private void deleteFolder(java.io.File file) {
        if (file == null || !file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            java.io.File[] children = file.listFiles();
            if (children != null) {
                for (java.io.File child : children) {
                    deleteFolder(child);
                }
            }
        }

        file.delete();
    }

}
