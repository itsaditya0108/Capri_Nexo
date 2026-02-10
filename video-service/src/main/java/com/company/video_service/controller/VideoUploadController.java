package com.company.video_service.controller;

import com.company.video_service.dto.VideoChunkUploadResponse;
import com.company.video_service.dto.VideoFinalizeJobResponse;
import com.company.video_service.dto.VideoUploadInitRequest;
import com.company.video_service.dto.VideoUploadInitResponse;
import com.company.video_service.dto.VideoUploadResultResponse;
import com.company.video_service.dto.VideoUploadStatusResponse;
import com.company.video_service.entity.UploadSessionStatus;
import com.company.video_service.entity.Video;
import com.company.video_service.entity.VideoUploadSession;
import com.company.video_service.repository.VideoRepository;
import com.company.video_service.repository.VideoUploadSessionRepository;
import com.company.video_service.service.VideoUploadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/videos/uploads")
public class VideoUploadController {

        private final VideoUploadService videoUploadService;
        private final VideoUploadSessionRepository sessionRepository;
        private final VideoRepository videoRepository;

        public VideoUploadController(VideoUploadService videoUploadService,
                        VideoUploadSessionRepository sessionRepository, VideoRepository videoRepository) {
                this.videoUploadService = videoUploadService;
                this.sessionRepository = sessionRepository;
                this.videoRepository = videoRepository;
        }

        @PostMapping("/init")
        public ResponseEntity<VideoUploadInitResponse> initUpload(
                        @RequestHeader("X-USER-ID") Long userId,
                        @Valid @RequestBody VideoUploadInitRequest request) {
                return ResponseEntity.ok(videoUploadService.initUpload(userId, request));
        }

        @PutMapping("/{uploadUid}/chunks/{chunkIndex}")
        public ResponseEntity<VideoChunkUploadResponse> uploadChunk(
                        @RequestHeader("X-USER-ID") Long userId,
                        @PathVariable String uploadUid,
                        @PathVariable Integer chunkIndex,
                        @RequestHeader(value = "X-Chunk-SHA256", required = false) String sha256,
                        @RequestBody byte[] chunkBytes) {
                return ResponseEntity.ok(
                                videoUploadService.uploadChunk(userId, uploadUid, chunkIndex, chunkBytes, sha256));
        }

        @GetMapping("/{uploadUid}/status")
        public ResponseEntity<VideoUploadStatusResponse> getUploadStatus(
                        @RequestHeader("X-USER-ID") Long userId,
                        @PathVariable String uploadUid) {
                return ResponseEntity.ok(videoUploadService.getUploadStatus(userId, uploadUid));
        }

        @PostMapping("/{uploadUid}/finalize")
        public ResponseEntity<VideoFinalizeJobResponse> finalizeUpload(
                        @RequestHeader("X-USER-ID") Long userId,
                        @PathVariable String uploadUid) {
                return ResponseEntity.ok(videoUploadService.finalizeUpload(userId, uploadUid));
        }

        @GetMapping("/{uploadUid}/result")
        public ResponseEntity<VideoUploadResultResponse> getUploadResult(
                        @RequestHeader("X-USER-ID") Long userId,
                        @PathVariable String uploadUid) {

                VideoUploadSession session = sessionRepository.findByUploadUid(uploadUid)
                                .orElseThrow(() -> new RuntimeException("UPLOAD_SESSION_NOT_FOUND"));

                if (!session.getUserId().equals(userId)) {
                        throw new RuntimeException("FORBIDDEN");
                }

                if (session.getStatus() == UploadSessionStatus.FAILED) {
                        return ResponseEntity.ok(
                                        new VideoUploadResultResponse(uploadUid, "FAILED", null,
                                                        session.getErrorMessage()));
                }

                if (session.getStatus() != UploadSessionStatus.COMPLETED) {
                        return ResponseEntity.ok(
                                        new VideoUploadResultResponse(uploadUid, session.getStatus().name(), null,
                                                        null));
                }

                Video video = videoRepository.findByUploadUid(uploadUid)
                                .orElseThrow(() -> new RuntimeException("VIDEO_NOT_FOUND"));

                return ResponseEntity.ok(
                                new VideoUploadResultResponse(uploadUid, "COMPLETED", video.getVideoUid(), null));
        }

}
