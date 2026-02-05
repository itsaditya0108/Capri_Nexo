package com.example.authapp.config;

import com.example.authapp.repository.UserRepository;
import com.example.authapp.security.JwtAuthenticationFilter;
import com.example.authapp.services.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.authapp.security.JwtAuthEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(
                        HttpSecurity http,
                        JwtService jwtService,
                        UserRepository userRepository,
                        com.example.authapp.repository.UserSessionRepository userSessionRepository,
                        JwtAuthEntryPoint entryPoint) throws Exception {

                JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, userRepository,
                                userSessionRepository);

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(request -> {
                                        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
                                        config.setAllowedOriginPatterns(java.util.List.of("*"));
                                        config.setAllowedMethods(
                                                        java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                                        config.setAllowedHeaders(java.util.List.of("*"));
                                        config.setAllowCredentials(true);
                                        return config;
                                }))
                                .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint))
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/validate-session").authenticated()
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/internal/users/by-ids").permitAll() // Internal
                                                                                                       // service call
                                                .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/images/**",
                                                                "/favicon.ico", "/error")
                                                .permitAll()
                                                .requestMatchers("/api/images/**").permitAll() // Pass-through to Image
                                                                                               // Service
                                                .requestMatchers("/actuator/**").permitAll()
                                                .anyRequest().authenticated())

                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
