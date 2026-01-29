package com.company.image_service.service.impl;

import com.company.image_service.dto.StoredImageResult;
import com.company.image_service.entity.Image;
import com.company.image_service.repository.ImageRepository;
import com.company.image_service.service.ImageService;
import com.company.image_service.util.FileStorageUtil;
import com.company.image_service.util.ImageValidationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional(readOnly = true)
public class ImageServiceImpl implements ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageRepository imageRepository;
    private final String storageBasePath;
    private final long maxUploadSize;
    private final int maxUploadCount;

    public ImageServiceImpl(
            ImageRepository imageRepository,
            @Value("${image.storage.base-path}") String storageBasePath,
            @Value("${image.upload.max-size}") long maxUploadSize,
            @Value("${image.upload.max-count}") int maxUploadCount) {
        this.imageRepository = imageRepository;
        this.storageBasePath = storageBasePath;
        this.maxUploadSize = maxUploadSize;
        this.maxUploadCount = maxUploadCount;
    }

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------

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

    // ------------------------------------------------------------------
    // DELETE (SOFT)
    // ------------------------------------------------------------------

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

    // ------------------------------------------------------------------
    // SINGLE IMAGE UPLOAD (WITH THUMBNAIL)
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public Image uploadImage(Long userId, MultipartFile file) {

        try {
            // 1️⃣ HARD validation (real image check)
            BufferedImage source = ImageValidationUtil.validateAndRead(file, maxUploadSize);

            // 2️⃣ Store image + thumbnail
            StoredImageResult result = FileStorageUtil.storeWithThumbnail(
                    file, userId, storageBasePath, source);

            // 3️⃣ Build metadata
            Image image = new Image();
            image.setUserId(userId);
            image.setOriginalFilename(file.getOriginalFilename());
            image.setStoredFilename(result.getStoredFilename());
            image.setStoragePath(result.getOriginalPath());
            image.setThumbnailPath(result.getThumbnailPath());
            image.setWidth(result.getWidth());
            image.setHeight(result.getHeight());
            image.setContentType(file.getContentType());
            image.setFileSize(file.getSize());
            image.setIsDeleted(false);

            return imageRepository.save(image);

        } catch (Exception ex) {
            throw new RuntimeException("Image upload failed", ex);
        }
    }

    // ------------------------------------------------------------------
    // MULTI IMAGE UPLOAD (TEMPORARILY DISABLED)
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public List<Image> uploadImages(Long userId, List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            throw new RuntimeException("No files provided");
        }

        if (files.size() > maxUploadCount) {
            throw new RuntimeException("Too many images uploaded at once");
        }

        List<StoredImageResult> storedResults = new ArrayList<>();
        List<Image> images = new ArrayList<>();

        try {
            logger.info("Starting bulk upload for user {} with {} files", userId, files.size());

            for (MultipartFile file : files) {

                String originalName = file.getOriginalFilename();

                // 0️⃣ DUPLICATE CHECK
                if (imageRepository.findByUserIdAndOriginalFilenameAndIsDeletedFalse(userId, originalName)
                        .isPresent()) {
                    logger.warn("Skipping duplicate file: {} for user {}", originalName, userId);
                    continue; // Skip this file
                }

                try {
                    // 1️⃣ Validate image FIRST
                    BufferedImage source = ImageValidationUtil.validateAndRead(file, maxUploadSize);

                    // 2️⃣ Store image + thumbnail
                    StoredImageResult result = FileStorageUtil.storeWithThumbnail(
                            file, userId, storageBasePath, source);

                    storedResults.add(result);

                    // 3️⃣ Build entity
                    Image image = new Image();
                    image.setUserId(userId);
                    image.setOriginalFilename(file.getOriginalFilename());
                    image.setStoredFilename(result.getStoredFilename());
                    image.setStoragePath(result.getOriginalPath());
                    image.setThumbnailPath(result.getThumbnailPath());
                    image.setWidth(result.getWidth());
                    image.setHeight(result.getHeight());
                    image.setContentType(file.getContentType());
                    image.setFileSize(file.getSize());
                    image.setIsDeleted(false);

                    images.add(image);

                } catch (Exception e) {
                    logger.error("Failed to process file during sync: {}", originalName, e);

                }
            }

            // 4️⃣ Save DB records for successful ones
            if (images.isEmpty()) {
                // If EVERYTHING failed, then maybe we should throw?
                // Or just return empty list.
                logger.warn("No valid images processed in batch for user {}", userId);
                return new ArrayList<>();
            }

            return imageRepository.saveAll(images);

        } catch (Exception ex) {

            // This outer catch now only catches unexpected errors OUTSIDE the loop
            // or if saveAll fails.

            // 5️⃣ Rollback filesystem for ANYTHING that was stored
            for (StoredImageResult r : storedResults) {
                deleteQuietly(r.getOriginalPath());
                deleteQuietly(r.getThumbnailPath());
            }

            throw new RuntimeException("Bulk image upload failed: " + ex.getMessage(), ex);
        }
    }

    // ------------------------------------------------------------------
    // DOWNLOAD (ORIGINAL IMAGE)
    // ------------------------------------------------------------------

    @Override
    public Resource downloadImage(Long imageId, Long userId) {

        Image image = imageRepository
                .findByIdAndUserIdAndIsDeletedFalse(imageId, userId)
                .orElseThrow(() -> new RuntimeException("Image not found or access denied"));

        Path fullPath = Paths.get(storageBasePath, image.getStoragePath());

        try {
            Resource resource = new UrlResource(fullPath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Image file not found");
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid image path", e);
        }
    }

    // ------------------------------------------------------------------
    // DEV ONLY — used for browser <img> rendering
    // ------------------------------------------------------------------

    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    public Resource downloadImageById(Long id) {

        Image image = getImageById(id);
        Path fullPath = Paths.get(storageBasePath, image.getStoragePath());

        try {
            Resource resource = new UrlResource(fullPath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not readable");
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid file path", e);
        }
    }

    // ------------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------------

    // private void validateSingleFile(MultipartFile file) {
    //
    // if (file == null || file.isEmpty()) {
    // throw new RuntimeException("Empty file");
    // }
    //
    // if (file.getSize() > maxUploadSize) {
    // throw new RuntimeException("File size exceeds limit");
    // }
    //
    // String contentType = file.getContentType();
    // if (contentType == null || !contentType.startsWith("image/")) {
    // throw new RuntimeException("Invalid image type");
    // }
    //
    // }

    // ------------------------------------------------------------------
    // DOWNLOAD THUMBNAIL
    // ------------------------------------------------------------------

    public Resource downloadThumbnail(Long imageId, Long userId) {

        Image image = imageRepository
                .findByIdAndUserIdAndIsDeletedFalse(imageId, userId)
                .orElseThrow(() -> new RuntimeException("Image not found or access denied"));

        Path fullPath = Paths.get(storageBasePath, image.getThumbnailPath());

        try {
            Resource resource = new UrlResource(fullPath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Thumbnail file not found");
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid thumbnail path", e);
        }
    }

    private void deleteQuietly(String relativePath) {
        try {
            Path path = Paths.get(storageBasePath, relativePath);
            Files.deleteIfExists(path);
        } catch (Exception ignored) {
        }
    }

}
