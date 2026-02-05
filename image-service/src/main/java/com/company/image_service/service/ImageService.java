package com.company.image_service.service;

import com.company.image_service.entity.Image;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

public interface ImageService {

    // -----------------------------
    // Upload
    // -----------------------------

    Image uploadImage(Long userId, MultipartFile file, String type);

    List<Image> uploadImages(Long userId, List<MultipartFile> files);

    // -----------------------------
    // Read
    // -----------------------------

    // Old method (can keep or deprecate)
    Page<Image> getUserImages(Long userId, Pageable pageable);

    // New filtered method
    Page<Image> getUserImages(Long userId, String type, Pageable pageable);

    Image getUserImage(Long imageId, Long userId);

    // -----------------------------
    // Delete (Soft)
    // -----------------------------

    Image softDeleteImage(Long imageId, Long userId);

    // -----------------------------
    // Download
    // -----------------------------
    Resource downloadImage(Long imageId, Long userId);

    Resource downloadThumbnail(Long imageId, Long userId);

    Image getImageById(Long id);

    Resource downloadImageById(Long id);

    // -----------------------------
    // Validation
    // -----------------------------
    boolean validateImage(Long imageId, Long userId);
}
