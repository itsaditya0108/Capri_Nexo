package com.company.image_service.controller;

import com.company.image_service.entity.ProfilePicture;
import com.company.image_service.service.ProfilePictureService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profile-picture")
public class ProfilePictureController {

    private final ProfilePictureService service;

    public ProfilePictureController(ProfilePictureService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProfilePicture> upload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(service.upload(userId, file));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ProfilePicture>> getHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(service.getHistory(userId));
    }

    @PutMapping("/{profilePictureId}/active")
    public ResponseEntity<Void> setActive(
            @PathVariable Long profilePictureId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        service.setProfilePicture(userId, profilePictureId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{profilePictureId}/view/{type}")
    public ResponseEntity<Resource> getView(
            @PathVariable Long profilePictureId,
            @PathVariable String type,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(service.getProfilePictureResource(userId, profilePictureId, type));
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
