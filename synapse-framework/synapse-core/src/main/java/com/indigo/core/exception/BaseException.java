package com.indigo.core.exception;

import lombok.Getter;

/**
 * @author 史偕成
 * @date 2025/04/24 21:57
 **/
public abstract class BaseException extends RuntimeException {
    @Getter
    private final String code;
    private final String message;
    @Getter
    private final Object[] args;

    public BaseException(String code, String message, Object... args) {
        super(message);
        this.code = code;
        this.message = message;
        this.args = args;
    }

    public BaseException(String code, String message, Throwable cause, Object... args) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.args = args;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
