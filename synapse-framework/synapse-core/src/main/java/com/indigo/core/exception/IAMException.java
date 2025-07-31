package com.indigo.core.exception;

import com.indigo.core.exception.enums.ErrorCode;

/**
 * System exception for system-level errors
 * 
 * @author 史偕成
 * @date 2025/04/24 21:57
 **/
public class IAMException extends BaseException {
    
    public IAMException(String code, String message, Object... args) {
        super(code, message, args);
    }
    
    public IAMException(String code, String message, Throwable cause, Object... args) {
        super(code, message, cause, args);
    }
    
    public IAMException(ErrorCode errorCode, Object... args) {
        super(errorCode.getCode(), errorCode.getMessage(), args);
    }
    
    public IAMException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.getCode(), errorCode.getMessage(), cause, args);
    }
} 