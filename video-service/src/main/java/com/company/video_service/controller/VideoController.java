package com.company.video_service.controller; // Package for video service controllers

import com.company.video_service.entity.Video; // Import Video entity
import com.company.video_service.repository.VideoRepository; // Import Video repository
import org.springframework.http.ResponseEntity; // Import ResponseEntity for HTTP responses
import org.springframework.web.bind.annotation.*; // Import Spring MVC annotations
import org.springframework.core.io.Resource; // Import Resource for file handling
import org.springframework.core.io.FileSystemResource; // Import FileSystemResource for file system access
import org.springframework.http.HttpHeaders; // Import HttpHeaders for response headers
import org.springframework.http.MediaType; // Import MediaType for content type
import org.springframework.http.MediaTypeFactory; // Import MediaTypeFactory for content type detection

import java.io.File; // Import File class
import java.util.List; // Import List interface

@RestController // Marks this class as a REST controller
@RequestMapping("/api/v1/videos") // Base URL for video-related endpoints
public class VideoController { // Controller class for managing video resources

        private final VideoRepository videoRepository; // Repository for video operations
        private final com.company.video_service.repository.VideoProcessingJobRepository jobRepository; // Repository for
                                                                                                       // processing
                                                                                                       // jobs

        @org.springframework.beans.factory.annotation.Value("${video.storage.final-path}") // Inject final storage path
                                                                                           // from properties
        private String finalStoragePath; // Variable to hold the storage path

        // Constructor for dependency injection
        public VideoController(VideoRepository videoRepository,
                        com.company.video_service.repository.VideoProcessingJobRepository jobRepository) {
                this.videoRepository = videoRepository; // Initialize video repository
                this.jobRepository = jobRepository; // Initialize job repository
        }

        // Endpoint to get currently processing video jobs
        @GetMapping("/processing-jobs")
        public ResponseEntity<List<com.company.video_service.entity.VideoProcessingJob>> getProcessingJobs(
                        @RequestHeader("X-USER-ID") Long userId) { // Extract User ID from header
                // Return list of jobs that are PENDING or RUNNING for the user
                return ResponseEntity.ok(jobRepository.findAllByUserIdAndStatusIn(
                                userId,
                                List.of(com.company.video_service.entity.VideoProcessingJobStatus.PENDING,
                                                com.company.video_service.entity.VideoProcessingJobStatus.RUNNING)));
        }

        // Endpoint to get all videos for a user
        @GetMapping
        public ResponseEntity<List<Video>> getAllVideos(@RequestHeader("X-USER-ID") Long userId) { // Extract User ID
                // Return list of non-deleted videos for the user, sorted by creation date
                return ResponseEntity.ok(
                                videoRepository.findAllByUserIdAndIsDeletedFalseOrderByCreatedTimestampDesc(userId));
        }

        // Endpoint to stream a specific video file
        @GetMapping("/{videoUid}")
        public ResponseEntity<Resource> streamVideo(@PathVariable String videoUid) { // Extract video UID from URL
                // Find video by UID, throw exception if not found
                Video video = videoRepository.findByVideoUid(videoUid)
                                .orElseThrow(() -> new RuntimeException("VIDEO_NOT_FOUND"));

                String originalPath = video.getOriginalFilePath(); // Get original file path from entity
                File file; // Declare file object
                // Check if the path is absolute
                if (new File(originalPath).isAbsolute()) {
                        file = new File(originalPath); // Create file from absolute path
                } else {
                        file = new File(finalStoragePath, originalPath); // Create file relative to storage path
                }
                // Check if file exists on disk
                if (!file.exists()) {
                        throw new RuntimeException("VIDEO_NOT_FOUND"); // Throw exception if file is missing
                }

                Resource resource = new FileSystemResource(file); // Create resource from file

                // Return file as response with appropriate content type and headers
                return ResponseEntity.ok()
                                .contentType(MediaTypeFactory.getMediaType(resource)
                                                .orElse(MediaType.APPLICATION_OCTET_STREAM)) // Determine MIME type
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "inline; filename=\"" + video.getVideoUid() + ".mp4\"") // Set content
                                                                                                        // disposition
                                .body(resource); // Set file resource as body
        }
}
