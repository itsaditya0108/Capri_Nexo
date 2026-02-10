package com.company.video_service.repository;

import com.company.video_service.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {

    Optional<Video> findByVideoUid(String videoUid);

    boolean existsByVideoUid(String videoUid);

    java.util.Optional<Video> findByUploadUid(String uploadUid);

    boolean existsByUserIdAndTitleAndOriginalFileSizeAndIsDeletedFalse(Long userId, String title,
            Long originalFileSize);

    java.util.List<Video> findAllByUserIdAndIsDeletedFalseOrderByCreatedTimestampDesc(Long userId);
}
