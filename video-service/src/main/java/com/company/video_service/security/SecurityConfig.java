package com.company.video_service.security; // Package for security configurations

import org.springframework.context.annotation.Bean; // Bean annotation
import org.springframework.context.annotation.Configuration; // Configuration annotation
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // HttpSecurity builder
import org.springframework.security.web.SecurityFilterChain; // Security filter chain

@Configuration // Marks this as a configuration class
public class SecurityConfig { // Basic/Default Security Configuration (May be overridden by Profile-specific
                              // configs)

    @Bean // Define security filter chain
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow all requests (open access) - check if this conflicts with
                                                  // other configs
                );

        return http.build(); // Build chain
    }
}
