package com.company.image_service.dto;

import java.nio.file.Path;

public class StoredImageResult {

    private final String storedFilename;
    private final String originalPath;
    private final String thumbnailPath;
    private final int width;
    private final int height;

    public StoredImageResult(
            String storedFilename,
            String originalPath,
            String thumbnailPath,
            int width,
            int height
    ) {
        this.storedFilename = storedFilename;
        this.originalPath = originalPath;
        this.thumbnailPath = thumbnailPath;
        this.width = width;
        this.height = height;
    }

    public String getStoredFilename() { return storedFilename; }
    public String getOriginalPath() { return originalPath; }
    public String getThumbnailPath() { return thumbnailPath; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
