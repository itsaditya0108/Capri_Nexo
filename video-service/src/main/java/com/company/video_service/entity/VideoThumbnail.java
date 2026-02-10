package com.company.video_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_thumbnails")
public class VideoThumbnail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_thumbnail_id")
    private Long videoThumbnailId;

    @Column(name = "video_uid", nullable = false, length = 64)
    private String videoUid;

    @Column(name = "thumbnail_uid", nullable = false, unique = true, length = 64)
    private String thumbnailUid;

    @Column(name = "thumbnail_path", nullable = false, length = 500)
    private String thumbnailPath;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    public VideoThumbnail() {
    }

    @PrePersist
    public void onCreate() {
        this.createdTimestamp = LocalDateTime.now();

        if (this.isDefault == null) {
            this.isDefault = false;
        }
    }

    // ---------------- Getters / Setters ----------------

    public Long getVideoThumbnailId() {
        return videoThumbnailId;
    }

    public void setVideoThumbnailId(Long videoThumbnailId) {
        this.videoThumbnailId = videoThumbnailId;
    }

    public String getVideoUid() {
        return videoUid;
    }

    public void setVideoUid(String videoUid) {
        this.videoUid = videoUid;
    }

    public String getThumbnailUid() {
        return thumbnailUid;
    }

    public void setThumbnailUid(String thumbnailUid) {
        this.thumbnailUid = thumbnailUid;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
