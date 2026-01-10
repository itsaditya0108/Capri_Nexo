package com.company.usermicroservice.repository;

import com.company.usermicroservice.entity.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppConfigRepository
        extends JpaRepository<AppConfig, String> {
}
