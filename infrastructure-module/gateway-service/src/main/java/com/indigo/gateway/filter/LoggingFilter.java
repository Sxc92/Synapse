package com.indigo.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * Global filter for logging requests and responses
 * 
 * @author 史偕成
 * @date 2025/04/24 21:57
 **/
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final String REQUEST_ID_KEY = "RequestId";
    private static final String START_TIME_KEY = "StartTime";
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Generate a unique ID for the request
        String requestId = UUID.randomUUID().toString();
        
        // Record start time
        Instant startTime = Instant.now();
        
        // Add request ID to the exchange attributes for tracking
        exchange.getAttributes().put(REQUEST_ID_KEY, requestId);
        exchange.getAttributes().put(START_TIME_KEY, startTime);
        
        // Get request details
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String query = request.getURI().getRawQuery();
        String method = request.getMethod().name();
        
        // Add request ID to the request headers
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Request-ID", requestId)
                .build();
        
        // Replace the request with the modified one
        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
        
        // Log the request
        log.info("Request: {} {} [{}] - RequestId: {}", method, path + (query != null ? "?" + query : ""), 
                request.getRemoteAddress(), requestId);
        
        // Process the request and log the response details when complete
        return chain.filter(modifiedExchange)
                .doOnSuccess(v -> logResponse(modifiedExchange, startTime, requestId, null))
                .doOnError(ex -> logResponse(modifiedExchange, startTime, requestId, ex));
    }
    
    /**
     * Log response details
     */
    private void logResponse(ServerWebExchange exchange, Instant startTime, String requestId, Throwable ex) {
        // Calculate execution time
        long executionTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();
        
        // Get status code (if available)
        int statusCode = exchange.getResponse().getStatusCode() != null ?
                exchange.getResponse().getStatusCode().value() : 500;
        
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        
        if (ex == null) {
            log.info("Response: {} {} [{}] - Status: {} - Time: {}ms - RequestId: {}", 
                    method, path, request.getRemoteAddress(), statusCode, executionTime, requestId);
        } else {
            log.error("Response: {} {} [{}] - Status: {} - Time: {}ms - Error: {} - RequestId: {}", 
                    method, path, request.getRemoteAddress(), statusCode, executionTime, ex.getMessage(), requestId, ex);
        }
    }

    @Override
    public int getOrder() {
        // Set order to be the second-highest priority (after error handling)
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
} 