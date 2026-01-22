package com.example.authapp.dto;

import jakarta.validation.constraints.NotBlank;

public class SendPhoneOtpRequest {
    @NotBlank
    private String phone;

    // getters & setters

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
