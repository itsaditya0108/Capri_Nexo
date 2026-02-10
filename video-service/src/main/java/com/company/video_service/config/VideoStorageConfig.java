package com.company.video_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VideoStorageConfig {

    @Value("${video.storage.temp-path}")
    private String tmpUploadDir;

    @Value("${video.storage.final-path}")
    private String videoStorageDir;

    public String getTmpUploadDir() {
        return tmpUploadDir;
    }

    public String getVideoStorageDir() {
        return videoStorageDir;
    }
}
