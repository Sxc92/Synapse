package com.indigo.core.exception;

import com.indigo.core.exception.enums.ErrorCode;

/**
 * Business exception for common business logic errors
 * 
 * @author 史偕成
 * @date 2025/04/24 21:57
 **/
public class BusinessException extends BaseException {
    
    public BusinessException(String code, String message, Object... args) {
        super(code, message, args);
    }
    
    public BusinessException(String code, String message, Throwable cause, Object... args) {
        super(code, message, cause, args);
    }
    
    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode.getCode(), errorCode.getMessage(), args);
    }
    
    public BusinessException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.getCode(), errorCode.getMessage(), cause, args);
    }
} 