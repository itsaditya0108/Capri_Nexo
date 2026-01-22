package com.example.authapp.services;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MockSmsService implements SmsService {

    @Override
    public void sendOtp(String phone, String otp) {
        System.out.println(
                "📱 MOCK SMS OTP | Phone: " + phone + " | OTP: " + otp
        );
    }
}
