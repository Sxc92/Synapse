package com.indigo.core.exception;

import lombok.Getter;

/**
 * 线程相关异常
 *
 * @author 史偕成
 * @date 2025/05/14
 */
@Getter
public class ThreadException extends BaseException {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ERROR_CODE = "thread.error";

    public ThreadException(String message) {
        super(DEFAULT_ERROR_CODE, message);
    }

    public ThreadException(String message, Throwable cause) {
        super(DEFAULT_ERROR_CODE, message, cause);
    }

    public ThreadException(Throwable cause) {
        super(DEFAULT_ERROR_CODE, cause.getMessage(), cause);
    }

    public ThreadException(String code, String message) {
        super(code, message);
    }

    public ThreadException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
} 