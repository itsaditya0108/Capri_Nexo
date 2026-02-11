package com.company.video_service; // Package declaration for the video service

import org.springframework.boot.SpringApplication; // Import SpringApplication to run the app
import org.springframework.boot.autoconfigure.SpringBootApplication; // Import auto-configuration annotation
import org.springframework.scheduling.annotation.EnableScheduling; // Import scheduling support

@EnableScheduling // Enables Spring's scheduled task execution (e.g., cron jobs)
@SpringBootApplication // Marks this class as the main Spring Boot application and enables
                       // auto-configuration
public class VideoServiceApplication { // Main class definition

    // Main method: The entry point of the Java application
    public static void main(String[] args) {
        // Launches the Spring Boot application, setting up the container and context
        SpringApplication.run(VideoServiceApplication.class, args);
    }
}
