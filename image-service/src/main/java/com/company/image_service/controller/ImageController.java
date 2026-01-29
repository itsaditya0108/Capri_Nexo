package com.company.image_service.controller;

import com.company.image_service.entity.Image;
import com.company.image_service.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // 1️⃣ Get user's images (gallery)
    @GetMapping
    public Page<Image> getUserImages(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return imageService.getUserImages(userId, PageRequest.of(page, size));
    }

    // 2️⃣ Get single image metadata
    @GetMapping("/{id}")
    public Image getImage(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return imageService.getUserImage(id, userId);
    }

    // 3️⃣ Soft delete image
    @DeleteMapping("/{id}")
    public Image deleteImage(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return imageService.softDeleteImage(id, userId);
    }
}
