package com.example.authapp.repository;

import com.example.authapp.entity.User;
import com.example.authapp.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository
        extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByRefreshTokenHashAndIsRevokedFalse(
            String refreshTokenHash
    );



    @Modifying
    @Query("""
        UPDATE UserSession s
        SET s.isRevoked = true
        WHERE s.user.id = :userId
          AND s.isRevoked = false
    """)
    int revokeAllSessions(@Param("userId") Long userId);


    @Query("""
    SELECT s FROM UserSession s
    WHERE s.isRevoked = false
      AND s.refreshTokenHash IS NOT NULL
""")
    List<UserSession> findAllActiveSessions();

    @Query("""
    SELECT s FROM UserSession s
    WHERE s.user.id = :userId
      AND s.isRevoked = false
""")
    List<UserSession> findActiveSessions(@Param("userId") Long userId);
}
