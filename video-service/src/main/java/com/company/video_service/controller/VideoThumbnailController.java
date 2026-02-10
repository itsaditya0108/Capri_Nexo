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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@RestController
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*")
public class VideoThumbnailController {

    private static final Logger log = LoggerFactory.getLogger(VideoThumbnailController.class);

    private final VideoRepository videoRepository;

    @org.springframework.beans.factory.annotation.Value("${video.storage.final-path}")
    private String finalStoragePath;

    private final com.company.video_service.repository.VideoThumbnailRepository thumbnailRepository;

    public VideoThumbnailController(VideoRepository videoRepository,
            com.company.video_service.repository.VideoThumbnailRepository thumbnailRepository) {
        this.videoRepository = videoRepository;
        this.thumbnailRepository = thumbnailRepository;
    }

    @GetMapping("/api/v1/videos/{videoUid}/thumbnail")
    public ResponseEntity<Resource> getThumbnail(@PathVariable String videoUid) {
        log.debug("Fetching thumbnail for videoUid={}", videoUid);

        String thumbPath = null;

        // Try to find in video_thumbnails table first
        var vt = thumbnailRepository.findFirstByVideoUidAndIsDefaultTrue(videoUid);
        if (vt.isPresent()) {
            thumbPath = vt.get().getThumbnailPath();
            log.debug("Found in VideoThumbnail entity: {}", thumbPath);
        } else {
            log.debug("Not found in VideoThumbnail entity, falling back to Video entity.");
            // Fallback to Video entity
            Video video = videoRepository.findByVideoUid(videoUid)
                    .orElseThrow(() -> new RuntimeException("VIDEO_NOT_FOUND"));

            if (video.getThumbnailFilePath() == null) {
                log.warn("Video entity has no thumbnail path for videoUid={}", videoUid);
                throw new RuntimeException("THUMBNAIL_NOT_READY");
            }
            thumbPath = video.getThumbnailFilePath();
            log.debug("Found in Video entity: {}", thumbPath);
        }

        File file;
        if (new File(thumbPath).isAbsolute()) {
            file = new File(thumbPath);
        } else {
            file = new File(finalStoragePath, thumbPath);
        }

        log.debug("Resolved file path: {}", file.getAbsolutePath());

        if (!file.exists()) {
            log.error("File does not exist on disk: {}", file.getAbsolutePath());
            throw new RuntimeException("THUMBNAIL_FILE_NOT_FOUND");
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(resource);
    }
}
