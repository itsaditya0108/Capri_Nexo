package com.company.chat.dto;

public record UserSearchResponse(
        Long userId,
        String name,
        String email,
        String phone) {
}
