package com.example.authapp.dto;

public class VerifyNewDeviceRequest {

    private String email;
    private String deviceId;
    private String otp;
    private boolean logoutOtherDevices;

    public String getEmail() {
        return email;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getOtp() {
        return otp;
    }

    public boolean isLogoutOtherDevices() {
        return logoutOtherDevices;
    }
}
