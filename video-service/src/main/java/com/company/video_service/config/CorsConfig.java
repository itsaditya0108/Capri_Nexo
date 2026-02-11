package com.company.video_service.config; // Package for configuration classes

import org.springframework.context.annotation.Configuration; // Configuration annotation

@Configuration // Marks this class as a Spring configuration class
public class CorsConfig { // Legacy or alternative CORS configuration (currently unused)

    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    // return new WebMvcConfigurer() {
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    // registry.addMapping("/**") // Apply to all endpoints
    // .allowedOrigins("*") // Allow all origins
    // .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow standard
    // methods
    // .allowedHeaders("*"); // Allow all headers
    // }
    // };
    // }
}
