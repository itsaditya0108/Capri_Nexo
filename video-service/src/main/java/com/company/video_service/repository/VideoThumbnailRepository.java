package com.company.video_service.repository;
import com.company.video_service.entity.VideoThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoThumbnailRepository extends JpaRepository<VideoThumbnail, Long> {

    List<VideoThumbnail> findByVideoUid(String videoUid);

    Optional<VideoThumbnail> findByThumbnailUid(String thumbnailUid);

    Optional<VideoThumbnail> findFirstByVideoUidAndIsDefaultTrue(String videoUid);

    void deleteByVideoUid(String videoUid);
}
