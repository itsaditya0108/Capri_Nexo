package com.company.video_service.dto;

public class VideoUploadResultResponse {

    private String uploadUid;
    private String status;
    private String videoUid;
    private String errorMessage;

    public VideoUploadResultResponse() {}

    public VideoUploadResultResponse(String uploadUid, String status, String videoUid, String errorMessage) {
        this.uploadUid = uploadUid;
        this.status = status;
        this.videoUid = videoUid;
        this.errorMessage = errorMessage;
    }

    public String getUploadUid() {
        return uploadUid;
    }

    public void setUploadUid(String uploadUid) {
        this.uploadUid = uploadUid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVideoUid() {
        return videoUid;
    }

    public void setVideoUid(String videoUid) {
        this.videoUid = videoUid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
