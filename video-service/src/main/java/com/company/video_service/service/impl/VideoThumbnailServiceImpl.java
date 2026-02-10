package com.company.video_service.service.impl;

import com.company.video_service.service.VideoThumbnailService;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class VideoThumbnailServiceImpl implements VideoThumbnailService {

    @org.springframework.beans.factory.annotation.Value("${video.ffmpeg.path:ffmpeg}")
    private String ffmpegPath;

    @Override
    public File generateThumbnail(File videoFile, File outputFile) {

        try {
            // ffmpeg -i input.mp4 -ss 00:00:01 -vframes 1 thumbnail.jpg
            ProcessBuilder processBuilder = new ProcessBuilder(
                    ffmpegPath,
                    "-i", videoFile.getAbsolutePath(),
                    "-ss", "00:00:01",
                    "-vframes", "1",
                    "-y",
                    outputFile.getAbsolutePath());

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("FFMPEG_THUMBNAIL_FAILED exitCode=" + exitCode);
            }

            if (!outputFile.exists()) {
                throw new RuntimeException("THUMBNAIL_NOT_CREATED");
            }

            return outputFile;

        } catch (Exception e) {
            throw new RuntimeException("THUMBNAIL_GENERATION_ERROR: " + e.getMessage(), e);
        }
    }
}
