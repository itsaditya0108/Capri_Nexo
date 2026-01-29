package com.company.image_service.service.impl;

import com.company.image_service.entity.Image;
import com.company.image_service.repository.ImageRepository;
import com.company.image_service.service.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Page<Image> getUserImages(Long userId, Pageable pageable) {
        return imageRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
    }

    @Override
    public Image getUserImage(Long imageId, Long userId) {
        return imageRepository
                .findByIdAndUserIdAndIsDeletedFalse(imageId, userId)
                .orElseThrow(() -> new RuntimeException("Image not found or access denied"));
    }

    @Override
    @Transactional
    public Image softDeleteImage(Long imageId, Long userId) {

        Image image = imageRepository
                .findByIdAndUserIdAndIsDeletedFalse(imageId, userId)
                .orElseThrow(() -> new RuntimeException("Image not found or access denied"));

        image.setIsDeleted(true);
        image.setDeletedTimestamp(LocalDateTime.now());

        return imageRepository.save(image);
    }
}
