package com.company.image_service.repository;

import com.company.image_service.entity.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilePictureRepository
        extends JpaRepository<ProfilePicture, Long> {

    Optional<ProfilePicture> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
