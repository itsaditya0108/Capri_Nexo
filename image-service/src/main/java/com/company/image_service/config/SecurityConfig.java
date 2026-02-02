package com.company.image_service.config;

import com.company.image_service.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Profile("prod")
public class SecurityConfig {

        @org.springframework.beans.factory.annotation.Value("${auth.service.base-url}")
        private String authServiceBaseUrl;

        @Bean
        public SecurityFilterChain filterChain(
                        HttpSecurity http,
                        JwtAuthenticationFilter jwtFilter) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.disable()) // Prod relies on gateway or specific allowed origins if
                                                              // needed, but user asked for disable here
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/api/**").authenticated()
                                                .anyRequest().permitAll())
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
