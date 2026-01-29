package com.company.image_service.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.UUID;

public class FileStorageUtil {

    public static StoredFile store(
            MultipartFile file,
            Long userId,
            String basePath
    ) throws IOException {

        LocalDate now = LocalDate.now();

        Path userDir = Paths.get(
                basePath,
                "users",
                userId.toString(),
                "images",
                String.valueOf(now.getYear()),
                String.format("%02d", now.getMonthValue())
        );

        Files.createDirectories(userDir);

        String extension = getExtension(file.getOriginalFilename());
        String storedFilename = UUID.randomUUID() + extension;

        Path destination = userDir.resolve(storedFilename);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return new StoredFile(
                storedFilename,
                destination.toString()
        );
    }

    private static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public record StoredFile(String storedFilename, String fullPath) {}
}
