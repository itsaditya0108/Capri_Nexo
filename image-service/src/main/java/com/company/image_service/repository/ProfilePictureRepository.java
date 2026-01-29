package com.company.image_service.repository;

import com.company.image_service.entity.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilePictureRepository
        extends JpaRepository<ProfilePicture, Long> {

    // Find the currently active profile picture
    Optional<ProfilePicture> findByUserIdAndIsActiveTrue(Long userId);

    // Find all profile pictures for a user (History)
    java.util.List<ProfilePicture> findByUserId(Long userId);

    // Unset active flag for all user's pictures
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE ProfilePicture p SET p.isActive = false WHERE p.userId = :userId")
    void unsetActiveProfilePicture(@org.springframework.data.repository.query.Param("userId") Long userId);

    // Fallback: Get most recent profile picture (for migration/default)
    Optional<ProfilePicture> findTopByUserIdOrderByCreatedTimestampDesc(Long userId);
}
