package com.company.image_service.config;

import com.company.image_service.security.JwtAuthenticationFilter;
import com.company.image_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@Profile("prod")
public class SecurityConfig {

        @Value("${auth.service.base-url}")
        private String authServiceBaseUrl;

        @Value("${security.jwt.secret}")
        private String jwtSecret;

        @Bean
        public JwtUtil jwtUtil() {
                return new JwtUtil(jwtSecret);
        }

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
                return new JwtAuthenticationFilter(jwtUtil, authServiceBaseUrl);
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                // Allow all origins for prod to enable cross-port/cross-host access
                configuration.setAllowedOriginPatterns(List.of("*"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public SecurityFilterChain filterChain(
                        HttpSecurity http,
                        JwtAuthenticationFilter jwtFilter) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/api/**").authenticated()
                                                .anyRequest().permitAll())
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}