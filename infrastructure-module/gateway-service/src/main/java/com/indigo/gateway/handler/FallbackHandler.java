package com.indigo.gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Gateway 回退处理器
 * 当下游服务不可用时提供友好的错误响应
 *
 * @author 史偕成
 * @date 2025/01/07
 */
@Slf4j
@Component
public class FallbackHandler {

    private final ObjectMapper objectMapper;

    public FallbackHandler(@Qualifier("synapseObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 通用服务回退处理
     */
    public Mono<ServerResponse> fallback(ServerRequest request) {
        log.warn("服务回退触发 - 路径: {}, 方法: {}", request.path(), request.method());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 503);
        response.put("message", "服务暂时不可用，请稍后重试");
        response.put("data", null);
        response.put("timestamp", System.currentTimeMillis());
        
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    /**
     * 认证服务回退处理
     */
    public Mono<ServerResponse> authFallback(ServerRequest request) {
        log.warn("认证服务回退触发 - 路径: {}", request.path());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 503);
        response.put("message", "认证服务暂时不可用，请稍后重试");
        response.put("data", null);
        response.put("timestamp", System.currentTimeMillis());
        
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    /**
     * 业务服务回退处理
     */
    public Mono<ServerResponse> businessFallback(ServerRequest request) {
        log.warn("业务服务回退触发 - 路径: {}", request.path());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 503);
        response.put("message", "业务服务暂时不可用，请稍后重试");
        response.put("data", null);
        response.put("timestamp", System.currentTimeMillis());
        
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    /**
     * 第三方服务回退处理
     */
    public Mono<ServerResponse> thirdPartyFallback(ServerRequest request) {
        log.warn("第三方服务回退触发 - 路径: {}", request.path());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 503);
        response.put("message", "第三方服务暂时不可用，请稍后重试");
        response.put("data", null);
        response.put("timestamp", System.currentTimeMillis());
        
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }
} 