package com.example.authapp.dto;

public class RegisterResponse {

    private Long id;
    private String name;
    private String email;
    private boolean emailVerified;
    private String message;

    public RegisterResponse(Long id, String name, String email,
                            boolean emailVerified, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.emailVerified = emailVerified;
        this.message = message;
    }

    // getters


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getMessage() {
        return message;
    }
}
