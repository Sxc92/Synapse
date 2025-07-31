package com.indigo.core.exception;

import com.indigo.core.exception.enums.ErrorCode;
import com.indigo.core.utils.MessageUtils;
import lombok.Getter;

/**
 * 限流异常
 * 当请求超出限流阈值时抛出此异常
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Getter
public class RateLimitException extends BaseException {


    public RateLimitException(String code, String message, Object... args) {
        super(code, message, args);
    }

    public RateLimitException(String code, String message, Throwable cause, Object... args) {
        super(code, message, cause, args);
    }

    public RateLimitException(ErrorCode errorCode, String code) {
        super(errorCode.getCode(), MessageUtils.getMessage(code));
    }

    public RateLimitException(ErrorCode errorCode, String code, Object... args) {
        super(errorCode.getCode(), MessageUtils.getMessage(code, args));
    }

    public RateLimitException(ErrorCode errorCode, Throwable cause, String code) {
        super(errorCode.getCode(), MessageUtils.getMessage(code), cause);
    }

    public RateLimitException(ErrorCode errorCode, Throwable cause, String code, Object... args) {
        super(errorCode.getCode(), MessageUtils.getMessage(code, args), cause, args);
    }
}