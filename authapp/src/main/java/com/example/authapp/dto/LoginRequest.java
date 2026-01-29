package com.example.authapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "PASSWORD_REQUIRED")
    private String password;
    private boolean logoutOtherDevices;
    private DeviceContextDto deviceContext; // OPTIONAL

    // getters & setters
    public boolean isLogoutOtherDevices() {
        return logoutOtherDevices;
    }

    public void setLogoutOtherDevices(boolean logoutOtherDevices) {
        this.logoutOtherDevices = logoutOtherDevices;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DeviceContextDto getDeviceContext() {
        return deviceContext;
    }

    public void setDeviceContext(DeviceContextDto deviceContext) {
        this.deviceContext = deviceContext;
    }
}
