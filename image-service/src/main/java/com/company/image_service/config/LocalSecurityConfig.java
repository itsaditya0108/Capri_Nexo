package com.company.image_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("!prod")
public class LocalSecurityConfig {

    @Bean
    public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable()) // Let CorsConfig handle CORS via WebMvcConfigurer
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow everything, let Manual FilterConfig handle Auth
                );

        return http.build();
    }
}
