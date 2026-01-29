package com.company.image_service.mapper;

import com.company.image_service.dto.ImageResponseDto;
import com.company.image_service.entity.Image;

public class ImageMapper {

    public static ImageResponseDto toDto(Image image) {

        ImageResponseDto dto = new ImageResponseDto();
        dto.setId(image.getId());
        dto.setOriginalFilename(image.getOriginalFilename());
        dto.setContentType(image.getContentType());
        dto.setFileSize(image.getFileSize());
        dto.setCreatedAt(image.getCreatedTimestamp());
        dto.setImageUrl("/api/images/" + image.getId() + "/download");

        return dto;
    }
}
