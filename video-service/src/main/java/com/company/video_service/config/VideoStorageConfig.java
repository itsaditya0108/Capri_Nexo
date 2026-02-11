package com.company.video_service.config; // Package for configuration classes

import org.springframework.beans.factory.annotation.Value; // Value annotation
import org.springframework.context.annotation.Configuration; // Configuration annotation

@Configuration // Marks this class as a Spring configuration class
public class VideoStorageConfig { // Configuration for video storage paths

    @Value("${video.storage.temp-path}") // Inject temporary storage path from properties
    private String tmpUploadDir;

    @Value("${video.storage.final-path}") // Inject final storage path from properties
    private String videoStorageDir;

    // Getter for temporary upload directory
    public String getTmpUploadDir() {
        return tmpUploadDir;
    }

    // Getter for final video storage directory
    public String getVideoStorageDir() {
        return videoStorageDir;
    }
}
