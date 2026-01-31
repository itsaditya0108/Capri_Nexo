package com.example.authapp.services;

import com.example.authapp.entity.OtpVerification;
import com.example.authapp.entity.User;
import com.example.authapp.entity.UserDevice;
import com.example.authapp.exception.ApiException;
import com.example.authapp.repository.OtpVerificationRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final int MAX_ATTEMPTS = 5;

    private final OtpVerificationRepository otpRepository;
    private final EmailService emailService;

    public OtpService(OtpVerificationRepository otpRepository,
            EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    public String generateOtp() {
        return String.valueOf(
                100000 + new SecureRandom().nextInt(900000));
    }

    public void validateOtp(OtpVerification otp, String inputOtp) {
        log.info("OTP_VALIDATE | target={} | type={} | input={} | actual={}",
                otp.getTarget(), otp.getOtpType(), inputOtp, otp.getOtpCode());

        if (otp.getLockedUntil() != null &&
                otp.getLockedUntil().isAfter(LocalDateTime.now())) {
            log.warn("OTP_VALIDATE | FAILED | LOCKED | target={}", otp.getTarget());
            throw new ApiException("OTP_LOCKED");
        }

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("OTP_VALIDATE | FAILED | EXPIRED | target={} | expiresAt={} | now={}",
                    otp.getTarget(), otp.getExpiresAt(), LocalDateTime.now());
            throw new ApiException("OTP_EXPIRED");
        }

        String normalizedInput = (inputOtp != null) ? inputOtp.trim() : "";
        if (!otp.getOtpCode().equals(normalizedInput)) {
            otp.setAttempts(otp.getAttempts() + 1);
            log.warn("OTP_VALIDATE | FAILED | MISMATCH | target={} | attempts={}",
                    otp.getTarget(), otp.getAttempts());

            if (otp.getAttempts() >= MAX_ATTEMPTS) {
                otp.setLockedUntil(
                        LocalDateTime.now().plusMinutes(10));
                otpRepository.save(otp);
            }

            throw new ApiException("INVALID_OTP");
        }
        log.info("OTP_VALIDATE | SUCCESS | target={}", otp.getTarget());
    }

    @Transactional
    public void sendNewDeviceOtp(User user, UserDevice device) {

        // Invalidate old NEW_DEVICE OTPs for this user
        otpRepository.invalidateOldOtps(
                user.getId(),
                OtpVerification.OtpType.NEW_DEVICE_VERIFICATION);

        String otp = generateOtp();

        OtpVerification entity = new OtpVerification();
        entity.setUser(user);
        entity.setTarget(user.getEmail());
        entity.setOtpType(OtpVerification.OtpType.NEW_DEVICE_VERIFICATION);
        entity.setOtpCode(otp);
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        otpRepository.save(entity);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

}
