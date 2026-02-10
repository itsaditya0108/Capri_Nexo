package com.company.video_service.service;

import java.io.File;

public interface VideoThumbnailService {
    File generateThumbnail(File videoFile, File outputFile);
}
