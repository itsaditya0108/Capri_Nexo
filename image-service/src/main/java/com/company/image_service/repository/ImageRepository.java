package com.company.image_service.repository;

import com.company.image_service.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Fetch images owned by a user (not deleted)
     */
    Page<Image> findByUserIdAndIsDeletedFalse(
            Long userId,
            Pageable pageable
    );

    /**
     * Fetch a single image by id, ensuring ownership
     */
    Optional<Image> findByIdAndUserIdAndIsDeletedFalse(
            Long id,
            Long userId
    );
}
