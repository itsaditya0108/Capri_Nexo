package com.example.authapp.services;

public interface SmsService {
    void sendOtp(String phone, String otp);
}
