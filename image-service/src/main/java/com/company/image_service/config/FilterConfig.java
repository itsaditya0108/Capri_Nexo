package com.company.image_service.config;

import com.company.image_service.security.JwtAuthenticationFilter;
import com.company.image_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {

        JwtUtil jwtUtil = new JwtUtil(jwtSecret);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);

        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);

        return registration;
    }
}
