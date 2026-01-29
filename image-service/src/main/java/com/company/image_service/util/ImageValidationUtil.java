package com.company.image_service.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

public final class ImageValidationUtil {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    private ImageValidationUtil() {
        // utility class
    }

    /**
     * Validates that the file is a real, readable image
     * and returns a decoded BufferedImage.
     */
    public static BufferedImage validateAndRead(
            MultipartFile file,
            long maxUploadSize) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Empty file");
        }

        if (file.getSize() > maxUploadSize) {
            throw new RuntimeException("File size exceeds limit");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !hasAllowedExtension(originalName)) {
            throw new RuntimeException("Unsupported image format");
        }

        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new RuntimeException("Invalid or corrupted image");
        }

        if (image.getWidth() <= 0 || image.getHeight() <= 0) {
            throw new RuntimeException("Invalid image dimensions");
        }

        return image;
    }

    private static boolean hasAllowedExtension(String filename) {
        String lower = filename.toLowerCase();
        return ALLOWED_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }
}
