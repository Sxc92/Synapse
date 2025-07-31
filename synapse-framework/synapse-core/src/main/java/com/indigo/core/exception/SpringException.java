package com.indigo.core.exception;

import lombok.Getter;

/**
 * Spring相关异常
 *
 * @author 史偕成
 * @date 2025/05/14
 */
@Getter
public class SpringException extends BaseException {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ERROR_CODE = "spring.error";

    public SpringException(String message) {
        super(DEFAULT_ERROR_CODE, message);
    }

    public SpringException(String message, Throwable cause) {
        super(DEFAULT_ERROR_CODE, message, cause);
    }

    public SpringException(Throwable cause) {
        super(DEFAULT_ERROR_CODE, cause.getMessage(), cause);
    }

    public SpringException(String code, String message) {
        super(code, message);
    }

    public SpringException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
} 