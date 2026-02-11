package com.company.video_service.repository; // Package for repositories

import com.company.video_service.entity.VideoThumbnail; // Thumbnail entity
import org.springframework.data.jpa.repository.JpaRepository; // JPA Repository

import java.util.List; // List interface
import java.util.Optional; // Optional container

// Repository interface for accessing VideoThumbnail data
public interface VideoThumbnailRepository extends JpaRepository<VideoThumbnail, Long> {

    // Find all thumbnails associated with a specific video
    List<VideoThumbnail> findByVideoUid(String videoUid);

    // Find a thumbnail by its unique ID
    Optional<VideoThumbnail> findByThumbnailUid(String thumbnailUid);

    // Retrieve the default thumbnail for a video (usually for display in lists)
    Optional<VideoThumbnail> findFirstByVideoUidAndIsDefaultTrue(String videoUid);

    // Delete all thumbnails associated with a video
    void deleteByVideoUid(String videoUid);
}
