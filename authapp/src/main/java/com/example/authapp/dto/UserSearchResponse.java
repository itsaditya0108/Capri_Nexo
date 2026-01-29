package com.example.authapp.dto;

public class UserSearchResponse {

    private Long userId;
    private String name;
    private String email;

    public UserSearchResponse(Long userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // getters

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
