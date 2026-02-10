package com.company.video_service.service;

import com.company.video_service.dto.*;

public interface VideoUploadService {

    VideoUploadInitResponse initUpload(Long userId, VideoUploadInitRequest request);

    VideoChunkUploadResponse uploadChunk(
            Long userId,
            String uploadUid,
            Integer chunkIndex,
            byte[] chunkBytes,
            String sha256
    );

    VideoUploadStatusResponse getUploadStatus(Long userId, String uploadUid);

    VideoFinalizeJobResponse finalizeUpload(Long userId, String uploadUid);
}
