package com.example.authapp.repository;

import com.example.authapp.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByResetToken(String resetToken);

    @Query("""
                SELECT u
                FROM User u
                WHERE (
                    LOWER(u.name) LIKE CONCAT(:q, '%')
                    OR u.phone LIKE CONCAT('%', :q, '%')
                    OR LOWER(u.email) LIKE CONCAT(:q, '%')
                )
                AND u.status.id = '01'
            """)
    List<User> searchUsers(
            @Param("q") String query,
            Pageable pageable);

}
