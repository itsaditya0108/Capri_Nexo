package com.company.video_service.schedular; // Package for scheduled tasks

import com.company.video_service.entity.VideoProcessingJob; // Job entity
import com.company.video_service.entity.VideoProcessingJobStatus; // Job status enum
import com.company.video_service.repository.VideoProcessingJobRepository; // Repository for jobs
import com.company.video_service.service.VideoMergeService; // Service for processing merge jobs
import org.springframework.scheduling.annotation.Scheduled; // Scheduled annotation
import org.springframework.stereotype.Component; // Component annotation

import java.util.List; // List interface

@Component // Registers this bean as a Spring Component
public class VideoJobScheduler { // Scheduler for background video processing tasks

    private final VideoProcessingJobRepository jobRepository; // Repo to access jobs
    private final VideoMergeService mergeService; // Service to execute merge logic

    // Constructor injection
    public VideoJobScheduler(VideoProcessingJobRepository jobRepository,
            VideoMergeService mergeService) {
        this.jobRepository = jobRepository;
        this.mergeService = mergeService;
    }

    // Runs every 5000ms (5 seconds) after the last execution finishes
    @Scheduled(fixedDelay = 5000)
    public void processMergeJobs() {

        // Fetch top 5 PENDING jobs, ordered by creation time (FCFS)
        List<VideoProcessingJob> jobs = jobRepository
                .findTop5ByStatusOrderByCreatedTimestampAsc(VideoProcessingJobStatus.PENDING);

        // Iterate and process each job
        for (VideoProcessingJob job : jobs) {
            try {
                // Delegate processing to the merge service
                mergeService.processMergeJob(job);
            } catch (Exception e) {
                // Should not happen as processMergeJob handles exceptions internally, but this
                // is a safety net
                System.err.println("Unexpected error processing job " + job.getJobUid() + ": " + e.getMessage());
            }
        }
    }
}
