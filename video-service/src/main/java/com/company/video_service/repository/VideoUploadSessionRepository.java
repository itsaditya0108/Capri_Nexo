package com.company.video_service.repository;

import com.company.video_service.entity.VideoUploadSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoUploadSessionRepository extends JpaRepository<VideoUploadSession, Long> {

    Optional<VideoUploadSession> findByUploadUid(String uploadUid);

    boolean existsByUploadUid(String uploadUid);

    java.util.Optional<VideoUploadSession> findFirstByUserIdAndOriginalFileNameAndOriginalFileSizeAndStatusInOrderByCreatedTimestampDesc(
            Long userId, String originalFileName, Long originalFileSize,
            java.util.List<com.company.video_service.entity.UploadSessionStatus> statuses);
}
