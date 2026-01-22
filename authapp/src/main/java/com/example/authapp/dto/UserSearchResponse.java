package com.example.authapp.dto;

public class UserSearchResponse {

    private Long userId;
    private String name;

    public UserSearchResponse(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    // getters


    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
