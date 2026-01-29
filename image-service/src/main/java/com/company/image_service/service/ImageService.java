package com.company.image_service.service;

import com.company.image_service.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ImageService {

    Page<Image> getUserImages(Long userId, Pageable pageable);

    Image getUserImage(Long imageId, Long userId);

    Image softDeleteImage(Long imageId, Long userId);
}
