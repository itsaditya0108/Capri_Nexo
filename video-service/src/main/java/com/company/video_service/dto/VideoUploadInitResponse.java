package com.company.video_service.dto;

import java.time.LocalDateTime;

public class VideoUploadInitResponse {

    private String uploadUid;
    private Integer chunkSizeBytes;
    private Integer totalChunks;
    private LocalDateTime expiresTimestamp;
    private String status;

    public VideoUploadInitResponse() {
    }

    public VideoUploadInitResponse(String uploadUid, Integer chunkSizeBytes, Integer totalChunks,
                                   LocalDateTime expiresTimestamp, String status) {
        this.uploadUid = uploadUid;
        this.chunkSizeBytes = chunkSizeBytes;
        this.totalChunks = totalChunks;
        this.expiresTimestamp = expiresTimestamp;
        this.status = status;
    }

    public String getUploadUid() {
        return uploadUid;
    }

    public void setUploadUid(String uploadUid) {
        this.uploadUid = uploadUid;
    }

    public Integer getChunkSizeBytes() {
        return chunkSizeBytes;
    }

    public void setChunkSizeBytes(Integer chunkSizeBytes) {
        this.chunkSizeBytes = chunkSizeBytes;
    }

    public Integer getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(Integer totalChunks) {
        this.totalChunks = totalChunks;
    }

    public LocalDateTime getExpiresTimestamp() {
        return expiresTimestamp;
    }

    public void setExpiresTimestamp(LocalDateTime expiresTimestamp) {
        this.expiresTimestamp = expiresTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
