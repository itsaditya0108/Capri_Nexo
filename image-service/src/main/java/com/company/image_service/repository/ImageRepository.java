package com.company.image_service.repository;

import com.company.image_service.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

        /**
         * Fetch images owned by a user (not deleted)
         */
        Page<Image> findByUserIdAndIsDeletedFalse(
                        Long userId,
                        Pageable pageable);

        /**
         * Fetch a single image by id, ensuring ownership
         */
        Optional<Image> findByIdAndUserIdAndIsDeletedFalse(
                        Long id,
                        Long userId);

        /**
         * Check for duplicate filename for a user
         */
        Optional<Image> findByUserIdAndOriginalFilenameAndIsDeletedFalse(
                        Long userId,
                        String originalFilename);

        /**
         * Delete image by id, ensuring ownership
         */
        /**
         * Delete image by id, ensuring ownership
         */
        List<Image> findByIsDeletedTrueAndDeletedTimestampBefore(
                        LocalDateTime cutoff,
                        Pageable pageable);

        /**
         * Filter by storage path prefix (e.g. "users/" vs "shared_images/")
         */
        Page<Image> findByUserIdAndIsDeletedFalseAndStoragePathStartingWith(
                        Long userId,
                        String pathPrefix,
                        Pageable pageable);

}
