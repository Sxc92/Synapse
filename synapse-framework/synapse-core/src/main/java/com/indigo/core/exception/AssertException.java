package com.indigo.core.exception;

import com.indigo.core.exception.enums.ErrorCode;
import com.indigo.core.utils.MessageUtils;

/**
 * 断言异常，用于参数校验和业务断言
 * 
 * @author 史偕成
 * @date 2024/03/21
 **/
public class AssertException extends BaseException {
    
    public AssertException(String code) {
        super(ErrorCode.PARAM_ERROR.getCode(), MessageUtils.getMessage(code));
    }
    
    public AssertException(String code, Object... args) {
        super(ErrorCode.PARAM_ERROR.getCode(), MessageUtils.getMessage(code, args));
    }
    
    public AssertException(ErrorCode errorCode, String code) {
        super(errorCode.getCode(), MessageUtils.getMessage(code));
    }
    
    public AssertException(ErrorCode errorCode, String code, Object... args) {
        super(errorCode.getCode(), MessageUtils.getMessage(code, args));
    }
    
    public AssertException(ErrorCode errorCode, Throwable cause, String code) {
        super(errorCode.getCode(), MessageUtils.getMessage(code), cause);
    }
    
    public AssertException(ErrorCode errorCode, Throwable cause, String code, Object... args) {
        super(errorCode.getCode(), MessageUtils.getMessage(code, args), cause, args);
    }
} 