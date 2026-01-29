package com.company.image_service.util;

import com.company.image_service.dto.StoredImageResult;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.UUID;

public class FileStorageUtil {

    private static final int THUMB_SIZE = 300;

    public static StoredImageResult storeWithThumbnail(
            MultipartFile file,
            Long userId,
            String basePath,
            BufferedImage source
    ) throws IOException {

        LocalDate now = LocalDate.now();

        String extension = getExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String filename = uuid + extension;

        // -------------------------
        // Directories
        // -------------------------
        Path baseDir = Paths.get(
                basePath,
                "users",
                userId.toString(),
                "images",
                String.valueOf(now.getYear()),
                String.format("%02d", now.getMonthValue())
        );

        Path originalDir = baseDir.resolve("original");
        Path thumbDir = baseDir.resolve("thumb");

        Files.createDirectories(originalDir);
        Files.createDirectories(thumbDir);

        // -------------------------
        // Read image once
        // -------------------------
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new RuntimeException("Invalid image");
        }

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // -------------------------
        // Save ORIGINAL
        // -------------------------
        Path originalPath = originalDir.resolve(filename);
        ImageIO.write(originalImage, extension.substring(1), originalPath.toFile());

        // -------------------------
        // Create THUMBNAIL
        // -------------------------
        int crop = Math.min(width, height);
        int x = (width - crop) / 2;
        int y = (height - crop) / 2;

        BufferedImage cropped = originalImage.getSubimage(x, y, crop, crop);
        BufferedImage thumb = new BufferedImage(
                THUMB_SIZE,
                THUMB_SIZE,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = thumb.createGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );
        g.drawImage(cropped, 0, 0, THUMB_SIZE, THUMB_SIZE, null);
        g.dispose();

        Path thumbPath = thumbDir.resolve(filename);
        ImageIO.write(thumb, extension.substring(1), thumbPath.toFile());

        // -------------------------
        // Relative paths for DB
        // -------------------------
        String relativeOriginalPath = toRelativePath(basePath, originalPath);
        String relativeThumbPath = toRelativePath(basePath, thumbPath);

        return new StoredImageResult(
                filename,
                relativeOriginalPath,
                relativeThumbPath,
                width,
                height
        );
    }

    private static String toRelativePath(String basePath, Path fullPath) {
        return Paths.get(basePath)
                .relativize(fullPath)
                .toString()
                .replace("\\", "/");
    }

    private static String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot == -1) return "";
        return filename.substring(dot).toLowerCase();
    }
}
