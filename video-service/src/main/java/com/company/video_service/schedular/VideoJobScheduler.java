package com.company.video_service.schedular;

import com.company.video_service.entity.VideoProcessingJob;
import com.company.video_service.entity.VideoProcessingJobStatus;
import com.company.video_service.repository.VideoProcessingJobRepository;
import com.company.video_service.service.VideoMergeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VideoJobScheduler {

    private final VideoProcessingJobRepository jobRepository;
    private final VideoMergeService mergeService;

    public VideoJobScheduler(VideoProcessingJobRepository jobRepository,
            VideoMergeService mergeService) {
        this.jobRepository = jobRepository;
        this.mergeService = mergeService;
    }

    @Scheduled(fixedDelay = 5000)
    public void processMergeJobs() {

        List<VideoProcessingJob> jobs = jobRepository
                .findTop5ByStatusOrderByCreatedTimestampAsc(VideoProcessingJobStatus.PENDING);

        for (VideoProcessingJob job : jobs) {
            try {
                mergeService.processMergeJob(job);
            } catch (Exception e) {
                // Should not happen as processMergeJob handles exceptions, but just in case
                System.err.println("Unexpected error processing job " + job.getJobUid() + ": " + e.getMessage());
            }
        }
    }
}
