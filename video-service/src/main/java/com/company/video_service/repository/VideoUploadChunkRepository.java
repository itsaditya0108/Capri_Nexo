package com.company.video_service.repository; // Package for repositories

import com.company.video_service.entity.VideoUploadChunk; // Upload chunk entity
import org.springframework.data.jpa.repository.JpaRepository; // JPA Repository

import java.util.List; // List interface
import java.util.Optional; // Optional container

// Repository interface for accessing VideoUploadChunk data (tracking parts of a file during upload)
public interface VideoUploadChunkRepository extends JpaRepository<VideoUploadChunk, Long> {

    // Retrieve a specific chunk by upload session ID and chunk index
    Optional<VideoUploadChunk> findByUploadUidAndChunkIndex(String uploadUid, Integer chunkIndex);

    // Retrieve all chunks for a session, ordered by index (critical for reassembly)
    List<VideoUploadChunk> findByUploadUidOrderByChunkIndexAsc(String uploadUid);

    // Count how many chunks have been uploaded for a session
    long countByUploadUid(String uploadUid);

    // Delete a specific chunk (method signature seems redundant with
    // deleteAllByUploadUid if intended for single, but name implies all)
    // Actually, JpaRepository derives deleteBy[Field], so this deletes ALL chunks
    // with that uploadUid
    void deleteByUploadUid(String uploadUid);

    // Explicitly named method to delete all chunks for an upload session
    void deleteAllByUploadUid(String uploadUid);
}
