package com.company.video_service.dto;

public class VideoChunkUploadResponse {

    private String uploadUid;
    private Integer chunkIndex;
    private Long receivedBytes;
    private String status;

    public VideoChunkUploadResponse() {
    }

    public VideoChunkUploadResponse(String uploadUid, Integer chunkIndex, Long receivedBytes, String status) {
        this.uploadUid = uploadUid;
        this.chunkIndex = chunkIndex;
        this.receivedBytes = receivedBytes;
        this.status = status;
    }

    public String getUploadUid() {
        return uploadUid;
    }

    public void setUploadUid(String uploadUid) {
        this.uploadUid = uploadUid;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public Long getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(Long receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
