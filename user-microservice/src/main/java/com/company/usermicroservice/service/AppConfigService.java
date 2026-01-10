package com.company.usermicroservice.service;

import com.company.usermicroservice.entity.AppConfig;
import com.company.usermicroservice.repository.AppConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    private final AppConfigRepository repository;

    public AppConfigService(AppConfigRepository repository) {
        this.repository = repository;
    }

    public String getValue(String key) {
        return repository.findById(key)
                .map(AppConfig::getConfigValue)
                .orElseThrow(() ->
                        new RuntimeException("Missing config: " + key));
    }
}
