package com.company.image_service.controller;

import com.company.image_service.entity.ProfilePicture;
import com.company.image_service.service.ProfilePictureService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile-picture")
public class ProfilePictureController {

    private final ProfilePictureService service;

    public ProfilePictureController(ProfilePictureService service) {
        this.service = service;
    }

    @PostMapping
    public ProfilePicture upload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return service.upload(userId, file);
    }

    @GetMapping("/small")
    public ResponseEntity<Resource> small(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(service.getSmall(userId));
    }

    @GetMapping("/medium")
    public ResponseEntity<Resource> medium(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(service.getMedium(userId));
    }

    @GetMapping("/{userId}/small")
    public ResponseEntity<Resource> getSmall(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getSmall(userId));
    }

    @GetMapping("/{userId}/medium")
    public ResponseEntity<Resource> getMedium(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getMedium(userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        service.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
