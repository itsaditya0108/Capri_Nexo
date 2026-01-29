package com.company.image_service.service.impl;

import com.company.image_service.entity.ProfilePicture;
import com.company.image_service.repository.ProfilePictureRepository;
import com.company.image_service.service.ProfilePictureService;
import com.company.image_service.util.ProfilePictureStorageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

@Service
@Transactional
public class ProfilePictureServiceImpl implements ProfilePictureService {

    private final ProfilePictureRepository repository;
    private final String basePath;

    public ProfilePictureServiceImpl(
            ProfilePictureRepository repository,
            @Value("${image.storage.base-path}") String basePath) {
        this.repository = repository;
        this.basePath = basePath;
    }

    @Override
    public ProfilePicture upload(Long userId, MultipartFile file) {

        try {
            ProfilePictureStorageUtil.StoredProfilePicture stored = ProfilePictureStorageUtil.store(file, userId,
                    basePath);

            repository.findByUserId(userId)
                    .ifPresent(this::deleteFiles);

            ProfilePicture pic = repository
                    .findByUserId(userId)
                    .orElse(new ProfilePicture());

            pic.setUserId(userId);
            pic.setOriginalPath(stored.originalPath());
            pic.setSmallPath(stored.smallPath());
            pic.setMediumPath(stored.mediumPath());
            pic.setWidth(stored.width());
            pic.setHeight(stored.height());
            pic.setContentType(file.getContentType());
            pic.setFileSize(file.getSize());

            return repository.save(pic);

        } catch (Exception e) {
            throw new RuntimeException("Profile picture upload failed", e);
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

        ProfilePicture pic = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile picture not found"));

        try {
            Path path = Paths.get(basePath, pathFn.apply(pic));
            return new UrlResource(path.toUri());
        } catch (Exception e) {
            throw new RuntimeException("File load failed", e);
        }
    }

    @Override
    public void delete(Long userId) {
        repository.findByUserId(userId).ifPresent(pic -> {
            deleteFiles(pic);
            repository.delete(pic);
        });
    }

    private void deleteFiles(ProfilePicture pic) {
        delete(pic.getOriginalPath());
        delete(pic.getSmallPath());
        delete(pic.getMediumPath());
    }

    private void delete(String rel) {
        try {
            Files.deleteIfExists(Paths.get(basePath, rel));
        } catch (Exception ignored) {
        }
    }
}
