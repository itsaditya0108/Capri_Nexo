package com.company.video_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_stream_variants")
public class VideoStreamVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_stream_variant_id")
    private Long videoStreamVariantId;

    @Column(name = "video_uid", nullable = false, length = 64)
    private String videoUid;

    @Column(name = "variant_uid", nullable = false, unique = true, length = 64)
    private String variantUid;

    @Column(name = "resolution", nullable = false, length = 20)
    private String resolution;   // example: 240p, 360p, 720p

    @Column(name = "bitrate_kbps", nullable = false)
    private Integer bitrateKbps;

    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private VideoStreamFormat format;

    @Column(name = "stream_path", nullable = false, length = 500)
    private String streamPath;

    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    public VideoStreamVariant() {
    }

    @PrePersist
    public void onCreate() {
        this.createdTimestamp = LocalDateTime.now();
    }

    // ---------------- Getters / Setters ----------------

    public Long getVideoStreamVariantId() {
        return videoStreamVariantId;
    }

    public void setVideoStreamVariantId(Long videoStreamVariantId) {
        this.videoStreamVariantId = videoStreamVariantId;
    }

    public String getVideoUid() {
        return videoUid;
    }

    public void setVideoUid(String videoUid) {
        this.videoUid = videoUid;
    }

    public String getVariantUid() {
        return variantUid;
    }

    public void setVariantUid(String variantUid) {
        this.variantUid = variantUid;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Integer getBitrateKbps() {
        return bitrateKbps;
    }

    public void setBitrateKbps(Integer bitrateKbps) {
        this.bitrateKbps = bitrateKbps;
    }

    public VideoStreamFormat getFormat() {
        return format;
    }

    public void setFormat(VideoStreamFormat format) {
        this.format = format;
    }

    public String getStreamPath() {
        return streamPath;
    }

    public void setStreamPath(String streamPath) {
        this.streamPath = streamPath;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
