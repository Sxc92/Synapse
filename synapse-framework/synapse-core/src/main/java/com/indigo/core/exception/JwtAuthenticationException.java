package com.indigo.core.exception;

import lombok.Getter;

@Getter
public class JwtAuthenticationException extends RuntimeException {
    private final JwtErrorType errorType;
    private final String message;

    public JwtAuthenticationException(JwtErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
        this.message = message;
    }

    public enum JwtErrorType {
        INVALID_SIGNATURE("无效的签名"),
        TOKEN_EXPIRED("令牌已过期"),
        MALFORMED_TOKEN("令牌格式错误"),
        INVALID_TOKEN("无效的令牌"),
        UNSUPPORTED_TOKEN("不支持的令牌类型"),
        TOKEN_GENERATION_FAILED("令牌生成失败");

        private final String description;

        JwtErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 