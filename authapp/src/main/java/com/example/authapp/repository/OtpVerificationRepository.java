package com.example.authapp.repository;

import com.example.authapp.entity.OtpVerification;
import com.example.authapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification>
    findTopByTargetAndOtpTypeAndVerifiedFalseOrderByCreatedAtDesc(
            String target,
            OtpVerification.OtpType otpType
    );

    @Modifying
    @Query("""
        UPDATE OtpVerification o
        SET o.verified = true
        WHERE o.user.id = :userId
          AND o.otpType = :otpType
    """)
    void invalidateOldOtps(
            @Param("userId") Long userId,
            @Param("otpType") OtpVerification.OtpType otpType
    );
}
