package com.company.video_service.repository;

import com.company.video_service.entity.VideoUploadChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoUploadChunkRepository extends JpaRepository<VideoUploadChunk, Long> {

    Optional<VideoUploadChunk> findByUploadUidAndChunkIndex(String uploadUid, Integer chunkIndex);

    List<VideoUploadChunk> findByUploadUidOrderByChunkIndexAsc(String uploadUid);

    long countByUploadUid(String uploadUid);

    void deleteByUploadUid(String uploadUid);

    void deleteAllByUploadUid(String uploadUid);
}
