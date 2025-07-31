package com.indigo.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for the gateway
 * 
 * @author 史偕成
 * @date 2025/04/24 21:57
 **/
@Configuration
public class CorsConfiguration {
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        org.springframework.web.cors.CorsConfiguration corsConfig = new org.springframework.web.cors.CorsConfiguration();
        
        // Allow requests from these origins (adjust as needed)
        corsConfig.setAllowedOriginPatterns(List.of("*"));
        
        // Allow these HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allow these headers in the request
        corsConfig.setAllowedHeaders(List.of("*"));
        
        // Allow these headers to be exposed in the response
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization", "X-Request-ID", "X-Total-Count", "Content-Disposition"));
        
        // Allow cookies
        corsConfig.setAllowCredentials(true);
        
        // How long the browser should cache CORS response in seconds
        corsConfig.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // Apply CORS configuration to all paths
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
} 