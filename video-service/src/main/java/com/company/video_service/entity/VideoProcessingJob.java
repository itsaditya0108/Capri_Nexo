package com.company.video_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_processing_jobs")
public class VideoProcessingJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_processing_job_id")
    private Long videoProcessingJobId;

    @Column(name = "job_uid", nullable = false, unique = true, length = 64)
    private String jobUid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "video_uid", length = 64)
    private String videoUid;

    @Column(name = "upload_uid", nullable = false, length = 64)
    private String uploadUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 50)
    private VideoJobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private VideoProcessingJobStatus status = VideoProcessingJobStatus.PENDING;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 3;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "started_timestamp")
    private LocalDateTime startedTimestamp;

    @Column(name = "completed_timestamp")
    private LocalDateTime completedTimestamp;

    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "updated_timestamp", nullable = false)
    private LocalDateTime updatedTimestamp;

    public VideoProcessingJob() {
    }

    public VideoProcessingJob(String uploadUid) {
        this.uploadUid = uploadUid;
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdTimestamp = now;
        this.updatedTimestamp = now;

        if (this.retryCount == null) {
            this.retryCount = 0;
        }

        if (this.maxRetries == null) {
            this.maxRetries = 3;
        }

        if (this.status == null) {
            this.status = VideoProcessingJobStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedTimestamp = LocalDateTime.now();
    }

    // ---------------- Getters / Setters ----------------

    public Long getVideoProcessingJobId() {
        return videoProcessingJobId;
    }

    public void setVideoProcessingJobId(Long videoProcessingJobId) {
        this.videoProcessingJobId = videoProcessingJobId;
    }

    public String getJobUid() {
        return jobUid;
    }

    public void setJobUid(String jobUid) {
        this.jobUid = jobUid;
    }

    public String getVideoUid() {
        return videoUid;
    }

    public void setVideoUid(String videoUid) {
        this.videoUid = videoUid;
    }

    public VideoJobType getJobType() {
        return jobType;
    }

    public void setJobType(VideoJobType jobType) {
        this.jobType = jobType;
    }

    public VideoProcessingJobStatus getStatus() {
        return status;
    }

    public void setStatus(VideoProcessingJobStatus status) {
        this.status = status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(LocalDateTime startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public LocalDateTime getCompletedTimestamp() {
        return completedTimestamp;
    }

    public void setCompletedTimestamp(LocalDateTime completedTimestamp) {
        this.completedTimestamp = completedTimestamp;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public LocalDateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUploadUid() {
        return uploadUid;
    }

    public void setUploadUid(String uploadUid) {
        this.uploadUid = uploadUid;
    }
}
