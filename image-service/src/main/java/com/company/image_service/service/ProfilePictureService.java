package com.company.image_service.service;

import com.company.image_service.entity.ProfilePicture;
import org.springframework.core.io.Resource;

import org.springframework.web.multipart.MultipartFile;

public interface ProfilePictureService {

    ProfilePicture upload(Long userId, MultipartFile file);

    void setProfilePicture(Long userId, Long profilePictureId);

    java.util.List<ProfilePicture> getHistory(Long userId);

    Resource getSmall(Long userId);

    Resource getMedium(Long userId);

    Resource getOriginal(Long userId);

    void delete(Long userId);

    // Generic retrieval by ID
    Resource getProfilePictureResource(Long userId, Long profilePictureId, String type);
}
