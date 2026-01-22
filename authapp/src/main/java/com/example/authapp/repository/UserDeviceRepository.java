package com.example.authapp.repository;

import com.example.authapp.entity.User;
import com.example.authapp.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDeviceRepository
        extends JpaRepository<UserDevice, Long> {

    Optional<UserDevice> findByUserAndDeviceId(User user, String deviceId);

    @Query("""
    SELECT COUNT(d)
    FROM UserDevice d
    WHERE d.user.id = :userId
      AND d.deviceTrusted = true
""")
    long countTrustedDevices(@Param("userId") Long userId);

}
