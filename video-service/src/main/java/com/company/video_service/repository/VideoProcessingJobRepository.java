package com.company.video_service.repository;

import com.company.video_service.entity.VideoProcessingJobStatus;
import com.company.video_service.entity.VideoJobType;
import com.company.video_service.entity.VideoProcessingJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoProcessingJobRepository extends JpaRepository<VideoProcessingJob, Long> {

    Optional<VideoProcessingJob> findByJobUid(String jobUid);

    List<VideoProcessingJob> findByVideoUid(String videoUid);

    List<VideoProcessingJob> findByVideoUidAndJobType(String videoUid, VideoJobType jobType);

    List<VideoProcessingJob> findByStatus(VideoProcessingJobStatus status);

    List<VideoProcessingJob> findTop5ByStatusOrderByCreatedTimestampAsc(VideoProcessingJobStatus status);

    List<VideoProcessingJob> findAllByUserIdAndStatusIn(Long userId, List<VideoProcessingJobStatus> statuses);
}