package com.company.video_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public class VideoUploadStatusResponse {

    private String uploadUid;
    private String fileName;
    private Long fileSize;
    private Integer chunkSizeBytes;
    private Integer totalChunks;

    private List<Integer> uploadedChunks;
    private Integer uploadedCount;
    private Integer remainingChunks;

    private String status;
    private LocalDateTime expiresTimestamp;

    public VideoUploadStatusResponse() {
    }

    public String getUploadUid() {
        return uploadUid;
    }

    public void setUploadUid(String uploadUid) {
        this.uploadUid = uploadUid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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

    public List<Integer> getUploadedChunks() {
        return uploadedChunks;
    }

    public void setUploadedChunks(List<Integer> uploadedChunks) {
        this.uploadedChunks = uploadedChunks;
    }

    public Integer getUploadedCount() {
        return uploadedCount;
    }

    public void setUploadedCount(Integer uploadedCount) {
        this.uploadedCount = uploadedCount;
    }

    public Integer getRemainingChunks() {
        return remainingChunks;
    }

    public void setRemainingChunks(Integer remainingChunks) {
        this.remainingChunks = remainingChunks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getExpiresTimestamp() {
        return expiresTimestamp;
    }

    public void setExpiresTimestamp(LocalDateTime expiresTimestamp) {
        this.expiresTimestamp = expiresTimestamp;
    }
}
