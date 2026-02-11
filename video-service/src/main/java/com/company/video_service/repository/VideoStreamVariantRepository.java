package com.company.video_service.repository; // Package for repositories

import com.company.video_service.entity.VideoStreamVariant; // Stream variant entity
import com.company.video_service.entity.VideoStreamFormat; // Stream format enum
import org.springframework.data.jpa.repository.JpaRepository; // JPA Repository

import java.util.List; // List interface
import java.util.Optional; // Optional container

// Repository interface for accessing VideoStreamVariant data (e.g., HLS/DASH variants)
public interface VideoStreamVariantRepository extends JpaRepository<VideoStreamVariant, Long> {

    // Retrieve all stream variants for a specific video
    List<VideoStreamVariant> findByVideoUid(String videoUid);

    // Retrieve a specific variant by its unique ID
    Optional<VideoStreamVariant> findByVariantUid(String variantUid);

    // Find all variants of a specific format (e.g., HLS) for a video
    List<VideoStreamVariant> findByVideoUidAndFormat(String videoUid, VideoStreamFormat format);

    // Delete all variants associated with a video (used when deleting a video)
    void deleteByVideoUid(String videoUid);
}
