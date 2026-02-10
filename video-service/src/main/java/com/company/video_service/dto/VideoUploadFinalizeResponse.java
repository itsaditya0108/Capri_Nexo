package com.company.video_service.dto;

public class VideoUploadFinalizeResponse {

    private String uploadUid;
    private String videoUid;
    private String status;
    private String streamUrl;
    private String thumbnailUrl;
    private String processingStatus;

    public VideoUploadFinalizeResponse() {
    }

    public VideoUploadFinalizeResponse(String uploadUid, String videoUid, String status,
                                       String streamUrl, String thumbnailUrl, String processingStatus) {
        this.uploadUid = uploadUid;
        this.videoUid = videoUid;
        this.status = status;
        this.streamUrl = streamUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.processingStatus = processingStatus;
    }

    public String getUploadUid() {
        return uploadUid;
    }

    public void setUploadUid(String uploadUid) {
        this.uploadUid = uploadUid;
    }

    public String getVideoUid() {
        return videoUid;
    }

    public void setVideoUid(String videoUid) {
        this.videoUid = videoUid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }
}
