package com.indigo.core.exception;

import lombok.Getter;

/**
 * 日期时间相关异常
 *
 * @author 史偕成
 * @date 2025/05/14
 */
@Getter
public class DateTimeException extends BaseException {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ERROR_CODE = "datetime.error";

    public DateTimeException(String message) {
        super(DEFAULT_ERROR_CODE, message);
    }

    public DateTimeException(String message, Throwable cause) {
        super(DEFAULT_ERROR_CODE, message, cause);
    }

    public DateTimeException(Throwable cause) {
        super(DEFAULT_ERROR_CODE, cause.getMessage(), cause);
    }

    public DateTimeException(String code, String message) {
        super(code, message);
    }

    public DateTimeException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
} 