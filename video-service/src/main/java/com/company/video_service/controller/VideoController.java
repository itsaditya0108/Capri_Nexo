package com.company.video_service.controller;

import com.company.video_service.entity.Video;
import com.company.video_service.repository.VideoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {

        private final VideoRepository videoRepository;
        private final com.company.video_service.repository.VideoProcessingJobRepository jobRepository;

        @org.springframework.beans.factory.annotation.Value("${video.storage.final-path}")
        private String finalStoragePath;

        public VideoController(VideoRepository videoRepository,
                        com.company.video_service.repository.VideoProcessingJobRepository jobRepository) {
                this.videoRepository = videoRepository;
                this.jobRepository = jobRepository;
        }

        @GetMapping("/processing-jobs")
        public ResponseEntity<List<com.company.video_service.entity.VideoProcessingJob>> getProcessingJobs(
                        @RequestHeader("X-USER-ID") Long userId) {
                return ResponseEntity.ok(jobRepository.findAllByUserIdAndStatusIn(
                                userId,
                                List.of(com.company.video_service.entity.VideoProcessingJobStatus.PENDING,
                                                com.company.video_service.entity.VideoProcessingJobStatus.RUNNING)));
        }

        @GetMapping
        public ResponseEntity<List<Video>> getAllVideos(@RequestHeader("X-USER-ID") Long userId) {
                return ResponseEntity.ok(
                                videoRepository.findAllByUserIdAndIsDeletedFalseOrderByCreatedTimestampDesc(userId));
        }

        @GetMapping("/{videoUid}")
        public ResponseEntity<Resource> streamVideo(@PathVariable String videoUid) {
                Video video = videoRepository.findByVideoUid(videoUid)
                                .orElseThrow(() -> new RuntimeException("VIDEO_NOT_FOUND"));

                String originalPath = video.getOriginalFilePath();
                File file;
                if (new File(originalPath).isAbsolute()) {
                        file = new File(originalPath);
                } else {
                        file = new File(finalStoragePath, originalPath);
                }
                if (!file.exists()) {
                        throw new RuntimeException("VIDEO_NOT_FOUND");
                }

                Resource resource = new FileSystemResource(file);

                return ResponseEntity.ok()
                                .contentType(MediaTypeFactory.getMediaType(resource)
                                                .orElse(MediaType.APPLICATION_OCTET_STREAM))
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "inline; filename=\"" + video.getVideoUid() + ".mp4\"")
                                .body(resource);
        }
}
