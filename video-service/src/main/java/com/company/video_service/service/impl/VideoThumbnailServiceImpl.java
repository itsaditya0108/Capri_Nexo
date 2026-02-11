package com.company.video_service.service.impl; // Package for service implementations

import com.company.video_service.service.VideoThumbnailService; // Service interface
import org.springframework.stereotype.Service; // Service annotation

import java.io.File; // File class

@Service // Marks this as a Spring service
public class VideoThumbnailServiceImpl implements VideoThumbnailService { // Implementation of thumbnail service

    @org.springframework.beans.factory.annotation.Value("${video.ffmpeg.path:ffmpeg}") // Inject FFmpeg path, default to
                                                                                       // "ffmpeg"
    private String ffmpegPath;

    @Override
    public File generateThumbnail(File videoFile, File outputFile) { // Method to generate thumbnail

        try {
            // Construct FFmpeg command:
            // ffmpeg -i input.mp4 -ss 00:00:01 -vframes 1 thumbnail.jpg
            ProcessBuilder processBuilder = new ProcessBuilder(
                    ffmpegPath, // Command (ffmpeg)
                    "-i", videoFile.getAbsolutePath(), // Input file
                    "-ss", "00:00:01", // Seek to 1 second mark
                    "-vframes", "1", // Capture 1 frame
                    "-y", // Overwrite output file if exists
                    outputFile.getAbsolutePath()); // Output file path

            processBuilder.redirectErrorStream(true); // Redirect stderr to stdout to capture errors
            Process process = processBuilder.start(); // Start the process

            // Read output to prevent blocking/deadlock (buffers must be consumed)
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("FFMPEG: " + line); // Log FFmpeg output
                }
            }

            int exitCode = process.waitFor(); // Wait for process to complete

            if (exitCode != 0) { // Check for failure
                throw new RuntimeException("FFMPEG_THUMBNAIL_FAILED exitCode=" + exitCode);
            }

            if (!outputFile.exists()) { // Verify output file was created
                throw new RuntimeException("THUMBNAIL_NOT_CREATED");
            }

            return outputFile; // Return the generated file

        } catch (Exception e) {
            // Propagate exception with context
            throw new RuntimeException("THUMBNAIL_GENERATION_ERROR: " + e.getMessage(), e);
        }
    }
}
