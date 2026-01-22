package com.example.authapp.repository;

import com.example.authapp.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository
        extends JpaRepository<UserStatus, String> {
}
