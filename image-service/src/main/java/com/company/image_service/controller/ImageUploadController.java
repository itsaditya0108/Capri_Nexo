package com.company.image_service.controller;

import com.company.image_service.entity.Image;
import com.company.image_service.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    private final ImageService imageService;

    public ImageUploadController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public Image upload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return imageService.uploadImage(userId, file);
    }

    @PostMapping(
            value = "/bulk",
            consumes = "multipart/form-data"
    )
    public List<Image> uploadMultiple(
            @RequestParam("files") List<MultipartFile> files,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return imageService.uploadImages(userId, files);
    }

}
