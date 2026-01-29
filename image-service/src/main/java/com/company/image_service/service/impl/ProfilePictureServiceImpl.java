package com.company.image_service.service.impl;

import com.company.image_service.entity.ProfilePicture;
import com.company.image_service.repository.ProfilePictureRepository;
import com.company.image_service.service.ProfilePictureService;
import com.company.image_service.exception.ResourceNotFoundException;
import com.company.image_service.util.ImageValidationUtil;
import com.company.image_service.util.ProfilePictureStorageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

@Service
@Transactional
public class ProfilePictureServiceImpl implements ProfilePictureService {

    private final ProfilePictureRepository repository;
    private final String basePath;

    @Value("${image.storage.max-size:5242880}") // Default 5MB
    private long maxUploadSize;

    public ProfilePictureServiceImpl(
            ProfilePictureRepository repository,
            @Value("${image.storage.base-path}") String basePath) {
        this.repository = repository;
        this.basePath = basePath;
    }

    @Override
    @Transactional
    public ProfilePicture upload(Long userId, MultipartFile file) {

        try {
            // 1. Validate
            ImageValidationUtil.validateAndRead(file, maxUploadSize);

            // 2. Unset active
            repository.unsetActiveProfilePicture(userId);

            // 3. Store
            ProfilePictureStorageUtil.StoredProfilePicture stored = ProfilePictureStorageUtil.store(file, userId,
                    basePath);

            // 4. Save new active
            ProfilePicture pic = new ProfilePicture();
            pic.setUserId(userId);
            pic.setOriginalPath(stored.originalPath());
            pic.setSmallPath(stored.smallPath());
            pic.setMediumPath(stored.mediumPath());
            pic.setWidth(stored.width());
            pic.setHeight(stored.height());
            pic.setContentType(file.getContentType());
            pic.setFileSize(file.getSize());
            pic.setActive(true);

            return repository.save(pic);

        } catch (Exception e) {
            throw new RuntimeException("Profile picture upload failed", e);
        }
    }

    @Override
    @Transactional
    public void setProfilePicture(Long userId, Long profilePictureId) {

        ProfilePicture pic = repository.findById(profilePictureId)
                .orElseThrow(() -> new RuntimeException("Profile picture not found"));

        if (!pic.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        repository.unsetActiveProfilePicture(userId);

        pic.setActive(true);
        repository.save(pic);
    }

    @Override
    public java.util.List<ProfilePicture> getHistory(Long userId) {
        return repository.findByUserId(userId);
    }

    /**
     * Kept for backward compatibility with Service interface, but now just
     * redirects
     * to returning a ProfilePicture entity wrapped or similar if needed.
     * ACTUALLY, strict requirement: service interface changed to return Image in
     * previous step?
     * No, I need to check the interface. The interface was likely changed to return
     * ProfilePicture.
     * I should revert the interface change or adapt here.
     *
     * Let's check: The user want "ProfilePicture" entity usage.
     * So I will probably need to adjust the interface to return ProfilePicture
     * again.
     */

    // Implementing the methods expected by the interface (which I should revert to
    // return ProfilePicture)

    @Override
    public Resource getProfilePictureResource(Long userId, Long profilePictureId, String type) {
        ProfilePicture pic = repository.findById(profilePictureId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile picture not found"));

        if (!pic.getUserId().equals(userId)) {
            // Check if calling user matches owner.
            // CAUTION: This means ONLY the owner can view the history item details?
            // If so, history view works.
            // But if chat uses this endpoint (it shouldn't, uses generic getSmall), it's
            // fine.
            throw new RuntimeException("Unauthorized");
        }

        String pathStr = switch (type) {
            case "small" -> pic.getSmallPath();
            case "medium" -> pic.getMediumPath();
            default -> pic.getOriginalPath();
        };

        try {
            Path path = Paths.get(basePath, pathStr);
            return new UrlResource(path.toUri());
        } catch (Exception e) {
            throw new RuntimeException("File load failed", e);
        }
    }

    @Override
    public Resource getOriginal(Long userId) {
        return load(userId, ProfilePicture::getOriginalPath);
    }

    @Override
    public Resource getSmall(Long userId) {
        return load(userId, ProfilePicture::getSmallPath);
    }

    @Override
    public Resource getMedium(Long userId) {
        return load(userId, ProfilePicture::getMediumPath);
    }

    private Resource load(Long userId, Function<ProfilePicture, String> pathFn) {

        ProfilePicture pic = repository.findByUserIdAndIsActiveTrue(userId)
                .or(() -> repository.findTopByUserIdOrderByCreatedTimestampDesc(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Active profile picture not found"));

        try {
            Path path = Paths.get(basePath, pathFn.apply(pic));
            return new UrlResource(path.toUri());
        } catch (Exception e) {
            throw new RuntimeException("File load failed", e);
        }
    }

    @Override
    public void delete(Long userId) {
        // Soft delete or hard delete? "Old profile picture must NOT be lost" ->
        // Implicitly means don't delete history.
        // But if user explicitly requests delete?
        // For now, let's just unset active.
        repository.unsetActiveProfilePicture(userId);
    }
}
