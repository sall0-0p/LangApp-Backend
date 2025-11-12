package com.lordbucket.langlearn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Global CORS configuration for the Spring Boot application.
 * This is crucial for allowing the frontend (running on localhost:5173) 
 * to communicate with the backend (running on localhost:8080 or wherever).
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // 2. Allow all common HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 3. Allow all headers (including Authorization header for JWT)
        configuration.setAllowedHeaders(List.of("*"));

        // 4. Important for authentication: Allows cookies and credentials (like JWT headers)
        configuration.setAllowCredentials(true);

        // Set this configuration to apply to all API paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}