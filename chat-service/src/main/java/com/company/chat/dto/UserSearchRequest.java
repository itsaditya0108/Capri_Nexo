package com.company.chat.dto;

public record UserSearchRequest(
        String query,
        int limit
) {}
