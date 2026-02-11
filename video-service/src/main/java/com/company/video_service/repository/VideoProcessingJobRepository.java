package com.company.video_service.repository; // Package for repositories

import com.company.video_service.entity.VideoProcessingJobStatus; // Job status enum
import com.company.video_service.entity.VideoJobType; // Job type enum
import com.company.video_service.entity.VideoProcessingJob; // Job entity
import org.springframework.data.jpa.repository.JpaRepository; // JPA Repository interface

import java.util.List; // List interface
import java.util.Optional; // Optional container

// Repository interface for accessing VideoProcessingJob data
public interface VideoProcessingJobRepository extends JpaRepository<VideoProcessingJob, Long> {

    // Find a job by its unique global ID
    Optional<VideoProcessingJob> findByJobUid(String jobUid);

    // Find all jobs associated with a specific video
    List<VideoProcessingJob> findByVideoUid(String videoUid);

    // Find jobs for a video with a specific type (e.g., THUMBNAIL_GENERATION)
    List<VideoProcessingJob> findByVideoUidAndJobType(String videoUid, VideoJobType jobType);

    // Find all jobs with a specific status
    List<VideoProcessingJob> findByStatus(VideoProcessingJobStatus status);

    // Find the oldest 5 jobs with a specific status (e.g., PENDING) for processing
    // queue
    List<VideoProcessingJob> findTop5ByStatusOrderByCreatedTimestampAsc(VideoProcessingJobStatus status);

    // Find all jobs for a specific user with any of the given statuses
    List<VideoProcessingJob> findAllByUserIdAndStatusIn(Long userId, List<VideoProcessingJobStatus> statuses);
}