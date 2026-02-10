package com.company.video_service.controller;

import com.company.video_service.dto.TestSplitVideoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@RestController
@RequestMapping("/api/v1/videos/test")
public class TestChunkController {

    @PostMapping("/split")
    public ResponseEntity<String> splitVideoIntoChunks(@RequestBody TestSplitVideoRequest request) {

        String inputFilePath = request.getInputFilePath();
        String outputFolderPath = request.getOutputFolderPath();
        Integer chunkSizeBytes = request.getChunkSizeBytes();

        if (chunkSizeBytes == null || chunkSizeBytes <= 0) {
            chunkSizeBytes = 5 * 1024 * 1024; // default 5MB
        }

        File inputFile = new File(inputFilePath);

        if (!inputFile.exists()) {
            return ResponseEntity.badRequest().body("Input file not found: " + inputFilePath);
        }

        File outputFolder = new File(outputFolderPath);

        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        int chunkIndex = 0;

        try (FileInputStream fis = new FileInputStream(inputFile)) {

            byte[] buffer = new byte[chunkSizeBytes];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {

                File chunkFile = new File(outputFolderPath + "/" + chunkIndex + ".part");

                try (FileOutputStream fos = new FileOutputStream(chunkFile)) {
                    fos.write(buffer, 0, bytesRead);
                }

                chunkIndex++;
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error splitting file: " + e.getMessage());
        }

        return ResponseEntity.ok("Chunks created successfully. Total chunks: " + chunkIndex);
    }
}

