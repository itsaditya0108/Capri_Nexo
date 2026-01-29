package com.company.image_service.controller;

import com.company.image_service.dto.ImageResponseDto;
import com.company.image_service.dto.PageResponseDto;
import com.company.image_service.entity.Image;
import com.company.image_service.mapper.ImageMapper;
import com.company.image_service.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // ----------------------------
    // Single upload
    // ----------------------------
    @PostMapping
    public ImageResponseDto upload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");

        Image image = imageService.uploadImage(userId, file);

        return ImageMapper.toDto(image);
    }

    // ----------------------------
    // Multi upload
    // ----------------------------
    @PostMapping("/bulk")
    public ResponseEntity<List<Image>> uploadImages(
            @RequestParam("files") List<MultipartFile> files,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");

        return ResponseEntity.ok(
                imageService.uploadImages(userId, files)
        );
    }


    // ----------------------------
    // List images
    // ----------------------------
//    @GetMapping
//    public Page<ImageResponseDto> getImages(
//            Pageable pageable,
//            HttpServletRequest request
//    ) {
//        Long userId = (Long) request.getAttribute("userId");
//
//        return imageService.getUserImages(userId, pageable)
//                .map(ImageMapper::toDto);
//    }

    @GetMapping
    public PageResponseDto<ImageResponseDto> getImages(
            @PageableDefault(size = 20, sort = "createdTimestamp", direction = Sort.Direction.DESC)
            Pageable pageable,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");

        Page<ImageResponseDto> page = imageService
                .getUserImages(userId, pageable)
                .map(ImageMapper::toDto);

        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    // ----------------------------
    // Download image
    // ----------------------------
//    @GetMapping("/{id}/download")
//    public ResponseEntity<Resource> download(
//            @PathVariable Long id,
//            HttpServletRequest request
//    ) {
//        Long userId = (Long) request.getAttribute("userId");
//
//        Image image = imageService.getUserImage(id, userId);
//        Resource resource = imageService.downloadImage(id, userId);
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(image.getContentType()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
//                .body(resource);
//    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @PathVariable Long id
    ) {
        // DEV: no userId, no ownership check
        Image image = imageService.getImageById(id); // new method
        Resource resource = imageService.downloadImageById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }

        imageService.softDeleteImage(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/thumbnail")
    public ResponseEntity<Resource> downloadThumbnail(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }

        Resource resource = imageService.downloadThumbnail(id, userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/*")
                .body(resource);
    }


}

/// Image Controller Old
//@RestController
//@RequestMapping("/api/images")
//public class ImageControllerr {
//
//    private final ImageService imageService;
//
//    public ImageController(ImageService imageService) {
//        this.imageService = imageService;
//    }
//
//    // 1️⃣ Get user's images (gallery)
//    @GetMapping
//    public Page<Image> getUserImages(
//            HttpServletRequest request,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size
//    ) {
//        Long userId = (Long) request.getAttribute("userId");
//        return imageService.getUserImages(userId, PageRequest.of(page, size));
//    }
//
//    // 2️⃣ Get single image metadata
//    @GetMapping("/{id}")
//    public Image getImage(
//            @PathVariable Long id,
//            HttpServletRequest request
//    ) {
//        Long userId = (Long) request.getAttribute("userId");
//        return imageService.getUserImage(id, userId);
//    }
//
//    // 3️⃣ Soft delete image
//    @DeleteMapping("/{id}")
//    public Image deleteImage(
//            @PathVariable Long id,
//            HttpServletRequest request
//    ) {
//        Long userId = (Long) request.getAttribute("userId");
//        return imageService.softDeleteImage(id, userId);
//    }
//
//
//    // 4 Download image
//    @GetMapping("/{id}/download")
//    public ResponseEntity<Resource> downloadImage(
//            @PathVariable Long id,
//            HttpServletRequest request
//    ) {
//        Long userId = (Long) request.getAttribute("userId");
//
//        Resource resource = imageService.downloadImage(id, userId);
//
//        return ResponseEntity.ok()
//                .header(
//                        HttpHeaders.CONTENT_DISPOSITION,
//                        "inline"
//                )
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }
//
//}
