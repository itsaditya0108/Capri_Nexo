package com.company.chat.config;

import com.company.chat.security.JwtAuthenticationFilter;
import com.company.chat.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SecurityConfig.class);

        @Value("${security.jwt.secret}")
        private String jwtSecret;

        @Value("${auth.service.base-url}")
        private String authServiceBaseUrl;

        @jakarta.annotation.PostConstruct
        public void logAuthBaseUrl() {
                log.info("AUTH SERVICE BASE URL = {}", authServiceBaseUrl);
        }

        @Bean
        public JwtUtil jwtUtil() {
                return new JwtUtil(jwtSecret);
        }

        @Bean
        public SecurityFilterChain filterChain(
                        HttpSecurity http,
                        JwtUtil jwtUtil) throws Exception {

                http
                                // 🔒 Stateless API
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // 🌐 Enable CORS
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // ❌ No CSRF for APIs
                                .csrf(csrf -> csrf.disable())

                                // 🔐 Authorization
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/internal/**").authenticated()
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/api/sse/**").permitAll()
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/api/conversations/**").authenticated()
                                                .anyRequest().authenticated())

                                // 🚨 JWT filter
                                .addFilterBefore(
                                                new JwtAuthenticationFilter(jwtUtil, authServiceBaseUrl),
                                                UsernamePasswordAuthenticationFilter.class)

                                // ❗ Clear auth errors
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(
                                                                (req, res, ex1) -> res.sendError(
                                                                                HttpServletResponse.SC_UNAUTHORIZED,
                                                                                "Unauthorized")));

                return http.build();
        }

        @Bean
        public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
                org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();

                // Allow all origins for development (e.g. localhost:3000, localhost:5173, etc.)
                configuration.setAllowedOriginPatterns(java.util.List.of("*"));

                configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(java.util.List.of("*"));
                configuration.setAllowCredentials(true);

                org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
