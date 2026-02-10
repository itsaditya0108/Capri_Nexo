package com.company.video_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "video_upload_chunks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_upload_chunk",
                        columnNames = {"upload_uid", "chunk_index"}
                )
        }
)
public class VideoUploadChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_upload_chunk_id")
    private Long videoUploadChunkId;

    @Column(name = "upload_uid", nullable = false, length = 64)
    private String uploadUid;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(name = "chunk_file_path", nullable = false, length = 500)
    private String chunkFilePath;

    @Column(name = "chunk_size_bytes", nullable = false)
    private Long chunkSizeBytes;

    @Column(name = "sha256_checksum", nullable = false, length = 64)
    private String sha256Checksum;

    @Column(name = "uploaded_timestamp", nullable = false, updatable = false)
    private LocalDateTime uploadedTimestamp;

    public VideoUploadChunk() {
    }

    @PrePersist
    public void onCreate() {
        this.uploadedTimestamp = LocalDateTime.now();
    }

    // ---------------- Getters / Setters ----------------

    public Long getVideoUploadChunkId() {
        return videoUploadChunkId;
    }

    public void setVideoUploadChunkId(Long videoUploadChunkId) {
        this.videoUploadChunkId = videoUploadChunkId;
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

    public String getChunkFilePath() {
        return chunkFilePath;
    }

    public void setChunkFilePath(String chunkFilePath) {
        this.chunkFilePath = chunkFilePath;
    }

    public Long getChunkSizeBytes() {
        return chunkSizeBytes;
    }

    public void setChunkSizeBytes(Long chunkSizeBytes) {
        this.chunkSizeBytes = chunkSizeBytes;
    }

    public String getSha256Checksum() {
        return sha256Checksum;
    }

    public void setSha256Checksum(String sha256Checksum) {
        this.sha256Checksum = sha256Checksum;
    }

    public LocalDateTime getUploadedTimestamp() {
        return uploadedTimestamp;
    }

    public void setUploadedTimestamp(LocalDateTime uploadedTimestamp) {
        this.uploadedTimestamp = uploadedTimestamp;
    }
}

