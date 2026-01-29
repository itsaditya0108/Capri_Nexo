package com.example.authapp.dto;

public class VerifyNewDeviceRequest {

    private String email;
    private String deviceId;
    private String otp;
    private boolean logoutOtherDevices;
    private DeviceContextDto deviceContext;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean isLogoutOtherDevices() {
        return logoutOtherDevices;
    }

    public void setLogoutOtherDevices(boolean logoutOtherDevices) {
        this.logoutOtherDevices = logoutOtherDevices;
    }

    public DeviceContextDto getDeviceContext() {
        return deviceContext;
    }

    public void setDeviceContext(DeviceContextDto deviceContext) {
        this.deviceContext = deviceContext;
    }
}
