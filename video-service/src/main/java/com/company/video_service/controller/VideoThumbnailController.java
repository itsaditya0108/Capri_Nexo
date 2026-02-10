package com.company.video_service.controller;

import com.company.video_service.entity.Video;
import com.company.video_service.repository.VideoRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class VideoThumbnailController {

    private final VideoRepository videoRepository;

    @org.springframework.beans.factory.annotation.Value("${video.storage.final-path}")
    private String finalStoragePath;

    public VideoThumbnailController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @GetMapping("/api/v1/videos/{videoUid}/thumbnail")
    public ResponseEntity<Resource> getThumbnail(@PathVariable String videoUid) {

        Video video = videoRepository.findByVideoUid(videoUid)
                .orElseThrow(() -> new RuntimeException("VIDEO_NOT_FOUND"));

        if (video.getThumbnailFilePath() == null) {
            throw new RuntimeException("THUMBNAIL_NOT_READY");
        }

        File file = new File(finalStoragePath, video.getThumbnailFilePath());

        if (!file.exists()) {
            throw new RuntimeException("THUMBNAIL_FILE_NOT_FOUND");
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(resource);
    }
}
