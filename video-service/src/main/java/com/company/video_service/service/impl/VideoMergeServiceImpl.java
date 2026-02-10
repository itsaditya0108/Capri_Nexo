package com.company.video_service.service.impl;

import com.company.video_service.entity.UploadSessionStatus;
import com.company.video_service.entity.Video;
import com.company.video_service.entity.VideoProcessingJob;
import com.company.video_service.entity.VideoProcessingJobStatus;
import com.company.video_service.entity.VideoStatus;
import com.company.video_service.entity.VideoUploadSession;
import com.company.video_service.repository.*;
import com.company.video_service.service.VideoThumbnailService;
import com.company.video_service.service.VideoMergeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VideoMergeServiceImpl implements VideoMergeService {

    private final VideoUploadSessionRepository sessionRepository;
    private final VideoUploadChunkRepository chunkRepository;
    private final VideoRepository videoRepository;
    private final VideoProcessingJobRepository processingJobRepository;
    private final VideoThumbnailRepository thumbnailRepository;
    private final VideoThumbnailService thumbnailService;

    @Value("${video.storage.temp-path}")
    private String tempStoragePath;

    @Value("${video.storage.final-path}")
    private String finalStoragePath;

    public VideoMergeServiceImpl(VideoUploadSessionRepository sessionRepository,
            VideoUploadChunkRepository chunkRepository,
            VideoRepository videoRepository,
            VideoThumbnailService thumbnailService,
            VideoProcessingJobRepository processingJobRepository,
            VideoThumbnailRepository thumbnailRepository) {
        this.sessionRepository = sessionRepository;
        this.chunkRepository = chunkRepository;
        this.videoRepository = videoRepository;
        this.processingJobRepository = processingJobRepository;
        this.thumbnailRepository = thumbnailRepository;
        this.thumbnailService = thumbnailService;
    }

    @Override
    public void processMergeJob(VideoProcessingJob job) {
        String uploadUid = job.getUploadUid();

        // update job status RUNNING
        job.setStatus(VideoProcessingJobStatus.RUNNING);
        job.setStartedTimestamp(LocalDateTime.now());
        processingJobRepository.save(job);

        try {
            // merge chunks + create video + thumbnail
            mergeUpload(uploadUid, job);

            job.setStatus(VideoProcessingJobStatus.COMPLETED);
            job.setCompletedTimestamp(LocalDateTime.now());
            processingJobRepository.save(job);

        } catch (Exception e) {
            job.setStatus(VideoProcessingJobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            job.setCompletedTimestamp(LocalDateTime.now());
            processingJobRepository.save(job);
        }
    }

    private void mergeUpload(String uploadUid, VideoProcessingJob job) {
        System.out.println("DEBUG: Starting mergeUpload for uploadUid=" + uploadUid);

        VideoUploadSession session = sessionRepository.findByUploadUid(uploadUid)
                .orElseThrow(() -> new RuntimeException("UPLOAD_SESSION_NOT_FOUND"));

        if (session.getStatus() != UploadSessionStatus.MERGING) {
            throw new RuntimeException("UPLOAD_SESSION_NOT_IN_MERGING_STATE");
        }

        try {
            // output folder structure: users/<userId>/videos/<year>/<month>/<uploadUid>/
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String datePath = String.format("%d/%02d", now.getYear(), now.getMonthValue());
            String relativeBasePath = "users/" + session.getUserId() + "/videos/" + datePath + "/" + uploadUid;

            File outputDir = new File(finalStoragePath, relativeBasePath);

            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File finalVideoFile = new File(outputDir, "original.mp4");
            System.out.println("DEBUG: Merging to " + finalVideoFile.getAbsolutePath());

            // merge chunks
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(finalVideoFile))) {

                for (int i = 0; i < session.getTotalChunks(); i++) {
                    File chunkFile = new File(tempStoragePath + "/" + uploadUid + "/" + i + ".part");
                    if (!chunkFile.exists()) {
                        throw new RuntimeException("CHUNK_FILE_MISSING_" + i);
                    }
                    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(chunkFile))) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Merge failed with exception: " + e.getMessage());
                session.setStatus(UploadSessionStatus.FAILED);
                session.setErrorCode("MERGE_FAILED");
                session.setErrorMessage(e.getMessage());
                session.setUpdatedTimestamp(LocalDateTime.now());
                sessionRepository.save(session);
                throw new RuntimeException("MERGE_FAILED: " + e.getMessage(), e);
            }

            // validate merged size
            if (finalVideoFile.length() != session.getOriginalFileSize()) {
                System.out.println("DEBUG: Size mismatch. Expected=" + session.getOriginalFileSize() + " Actual="
                        + finalVideoFile.length());
                session.setStatus(UploadSessionStatus.FAILED);
                session.setErrorCode("MERGED_FILE_SIZE_MISMATCH");
                session.setErrorMessage(
                        "Expected=" + session.getOriginalFileSize() + " actual=" + finalVideoFile.length());
                session.setUpdatedTimestamp(LocalDateTime.now());
                sessionRepository.save(session);
                throw new RuntimeException("MERGED_FILE_SIZE_MISMATCH");
            }

            // update upload session
            session.setMergedFilePath(finalVideoFile.getAbsolutePath());
            session.setMergedFileSize(finalVideoFile.length());
            session.setMergedTimestamp(LocalDateTime.now());
            session.setStatus(UploadSessionStatus.COMPLETED);
            session.setUpdatedTimestamp(LocalDateTime.now());
            sessionRepository.save(session);

            // create video record
            String videoUid = UUID.randomUUID().toString();
            Video video = new Video();
            video.setVideoUid(videoUid);
            video.setUserId(session.getUserId());
            video.setUploadUid(uploadUid);
            video.setTitle(session.getOriginalFileName());
            video.setOriginalFilePath(toRelativePath(finalVideoFile.getAbsolutePath()));
            video.setOriginalFileSize(finalVideoFile.length());
            video.setMimeType(session.getMimeType());
            video.setDurationSeconds(session.getDurationSeconds());
            video.setVideoWidth(session.getVideoWidth());
            video.setVideoHeight(session.getVideoHeight());
            video.setStatus(VideoStatus.UPLOADED);
            video.setCreatedTimestamp(LocalDateTime.now());
            video.setUpdatedTimestamp(LocalDateTime.now());
            videoRepository.save(video);

            // update job with videoUid
            job.setVideoUid(videoUid);
            processingJobRepository.save(job);

            // generate thumbnail
            File thumbnailFile = new File(outputDir, "thumbnail.jpg");
            System.out.println("DEBUG: Generating thumbnail to " + thumbnailFile.getAbsolutePath());

            try {
                if (thumbnailService == null) {
                    throw new RuntimeException("ThumbnailService is null!");
                }
                thumbnailService.generateThumbnail(finalVideoFile, thumbnailFile);
                System.out.println("DEBUG: Thumbnail generation process completed.");

                if (!thumbnailFile.exists() || thumbnailFile.length() == 0) {
                    throw new RuntimeException("Thumbnail file not created or empty.");
                }

                String derivedThumbPath = toRelativePath(thumbnailFile.getAbsolutePath());
                video.setThumbnailFilePath(derivedThumbPath);
                video.setThumbnailStatus("READY");
                video.setThumbnailGeneratedTimestamp(LocalDateTime.now());
                video.setUpdatedTimestamp(LocalDateTime.now());
                videoRepository.save(video);

                // Save to VideoThumbnail entity
                com.company.video_service.entity.VideoThumbnail vt = new com.company.video_service.entity.VideoThumbnail();
                vt.setVideoUid(videoUid);
                vt.setThumbnailUid(UUID.randomUUID().toString());
                vt.setThumbnailPath(derivedThumbPath);
                vt.setWidth(640); // default/placeholder
                vt.setHeight(360);
                vt.setDefault(true);

                System.out.println("DEBUG: Attempting to save VideoThumbnail: " + vt.getThumbnailUid() + " for video: "
                        + videoUid);
                com.company.video_service.entity.VideoThumbnail savedVt = thumbnailRepository.saveAndFlush(vt);
                System.out.println("DEBUG: VideoThumbnail saved. ID: " + savedVt.getVideoThumbnailId());

            } catch (Exception e) {
                System.out.println("DEBUG: Thumbnail generation failed: " + e.getMessage());
                e.printStackTrace();

                video.setThumbnailStatus("FAILED");
                video.setUpdatedTimestamp(LocalDateTime.now());
                videoRepository.save(video);
            }

        } finally {
            // cleanup chunk files + chunk rows
            System.out.println("DEBUG: Cleaning up chunks for " + uploadUid);
            cleanupChunks(uploadUid);
        }
    }

    @org.springframework.transaction.annotation.Transactional
    private void cleanupChunks(String uploadUid) {
        try {
            File chunkDir = new File(tempStoragePath + "/" + uploadUid);

            if (chunkDir.exists()) {
                File[] files = chunkDir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        f.delete();
                    }
                }
                chunkDir.delete();
                System.out.println("DEBUG: Deleted temp chunk directory: " + chunkDir.getAbsolutePath());
            }

            chunkRepository.deleteAllByUploadUid(uploadUid);
            System.out.println("DEBUG: Deleted chunk records for uploadUid: " + uploadUid);

        } catch (Exception e) {
            // ignore cleanup failure (log only in production)
            System.out.println("Cleanup failed: " + e.getMessage());
        }
    }

    private String toRelativePath(String absolutePath) {
        if (absolutePath == null)
            return null;

        try {
            File baseFile = new File(finalStoragePath);
            File absFile = new File(absolutePath);

            String base = baseFile.getCanonicalPath();
            String abs = absFile.getCanonicalPath();

            // Case-insensitive check for Windows compatibility
            if (abs.toLowerCase().startsWith(base.toLowerCase())) {
                String relative = abs.substring(base.length());
                if (relative.startsWith(File.separator)) {
                    relative = relative.substring(1);
                }
                return relative.replace("\\", "/");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return absolutePath;
    }
}
