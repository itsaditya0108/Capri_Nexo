package com.company.image_service.service.impl;

import com.company.image_service.entity.Image;
import com.company.image_service.repository.ImageRepository;
import com.company.image_service.service.ImageCleanupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImageCleanupServiceImpl implements ImageCleanupService {

    private final ImageRepository imageRepository;
    private final String storageBasePath;
    private final int retentionDays;
    private final int batchSize;

    public ImageCleanupServiceImpl(
            ImageRepository imageRepository,
            @Value("${image.storage.base-path}") String storageBasePath,
            @Value("${image.cleanup.retention-days}") int retentionDays,
            @Value("${image.cleanup.batch-size}") int batchSize
    ) {
        this.imageRepository = imageRepository;
        this.storageBasePath = storageBasePath;
        this.retentionDays = retentionDays;
        this.batchSize = batchSize;
    }

    @Override
    @Transactional
    public void cleanupDeletedImages() {

        LocalDateTime cutoff =
                LocalDateTime.now().minusDays(retentionDays);

        List<Image> images =
                imageRepository.findByIsDeletedTrueAndDeletedTimestampBefore(
                        cutoff,
                        PageRequest.of(0, batchSize)
                );

        for (Image image : images) {
            deleteFileSafely(image);
            imageRepository.delete(image); // hard delete DB row
        }
    }

    private void deleteFileSafely(Image image) {
        try {
            Path path = Paths.get(storageBasePath, image.getStoragePath());
            Files.deleteIfExists(path);
        } catch (Exception ex) {
            // LOG ONLY — never crash cleanup job
            System.err.println("Failed to delete file: " + image.getStoragePath());
        }
    }
}
