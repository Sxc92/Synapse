package com.indigo.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indigo.core.entity.Result;
import com.indigo.core.exception.BusinessException;
import com.indigo.core.exception.IAMException;
import com.indigo.core.exception.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Gateway 全局异常处理器
 * 使用synapse-core统一配置的ObjectMapper
 * 
 * @author 史偕成
 * @date 2024/03/21
 **/
@Slf4j
@Order(-1)
@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalErrorHandler(@Qualifier("synapseObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        // Set content type
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status;
        String code;
        String message;

        if (ex instanceof ResponseStatusException responseStatusException) {
            status = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
            code = String.valueOf(status.value());
            message = responseStatusException.getReason();
        } else if (ex instanceof BusinessException businessException) {
            status = HttpStatus.BAD_REQUEST;
            code = businessException.getCode();
            message = businessException.getMessage();
        } else if (ex instanceof IAMException iamException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = iamException.getCode();
            message = iamException.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = ErrorCode.SYSTEM_ERROR.getCode();
            message = ErrorCode.SYSTEM_ERROR.getMessage();
        }

        // Set status code
        response.setStatusCode(status);

            try {
                // 使用统一的Result格式
                Result<Object> result = Result.error(code, message);

                // Log the error
                log.error("Gateway error: {} - {}", status, message, ex);

                // Convert error response to JSON
                String errorJson = objectMapper.writeValueAsString(result);
                
                // Create buffer
                byte[] bytes = errorJson.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);

                // Write response
                return response.writeWith(Mono.just(buffer));
            } catch (JsonProcessingException e) {
                log.error("Error serializing error response", e);
                return Mono.error(e);
            }
    }
} 