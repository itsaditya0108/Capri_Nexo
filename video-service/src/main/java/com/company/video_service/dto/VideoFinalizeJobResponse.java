package com.company.video_service.dto;

public class VideoFinalizeJobResponse {

    private String uploadUid;
    private String status;
    private String mergeJobUid;

    public VideoFinalizeJobResponse() {
    }

    public VideoFinalizeJobResponse(String uploadUid, String status, String mergeJobUid) {
        this.uploadUid = uploadUid;
        this.status = status;
        this.mergeJobUid = mergeJobUid;
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

    public String getMergeJobUid() {
        return mergeJobUid;
    }

    public void setMergeJobUid(String mergeJobUid) {
        this.mergeJobUid = mergeJobUid;
    }
}
