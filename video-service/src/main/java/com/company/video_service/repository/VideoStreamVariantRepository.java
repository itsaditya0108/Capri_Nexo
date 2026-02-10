package com.company.video_service.repository;

import com.company.video_service.entity.VideoStreamVariant;
import com.company.video_service.entity.VideoStreamFormat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoStreamVariantRepository extends JpaRepository<VideoStreamVariant, Long> {

    List<VideoStreamVariant> findByVideoUid(String videoUid);

    Optional<VideoStreamVariant> findByVariantUid(String variantUid);

    List<VideoStreamVariant> findByVideoUidAndFormat(String videoUid, VideoStreamFormat format);

    void deleteByVideoUid(String videoUid);
}
