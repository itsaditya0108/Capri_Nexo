package com.company.image_service.service;

import com.company.image_service.entity.ProfilePicture;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ProfilePictureService {

    ProfilePicture upload(Long userId, MultipartFile file);

    Resource getSmall(Long userId);

    Resource getMedium(Long userId);

    Resource getOriginal(Long userId);

    void delete(Long userId);
}
