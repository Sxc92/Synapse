package com.indigo.core.exception.handler;

import com.indigo.core.entity.Result;
import com.indigo.core.exception.*;
import com.indigo.core.exception.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * WebMVC 全局异常处理器
 * 处理传统 Servlet 环境下的所有异常
 * 
 * @author 史偕成
 * @date 2024/12/19
 **/
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcGlobalExceptionHandler {

    @ExceptionHandler(I18nException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleI18nException(I18nException e) {
        log.error("I18nException: code={}, message={}", e.getCode(), e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(IAMException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleIAMException(IAMException e) {
        log.error("IAMException: code={}, message={}", e.getCode(), e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException: message={}", e.getMessage());
        return Result.error(ErrorCode.ILLEGAL_STATE.getCode(), e.getMessage());
    }

    @ExceptionHandler(AssertException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleAssertException(AssertException e) {
        log.warn("AssertException: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RateLimitException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public Result<?> handleRateLimitException(RateLimitException e) {
        log.warn("RateLimitException: message={}", e.getMessage());
        return Result.error(ErrorCode.RATE_LIMIT_EXCEEDED.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("Unexpected error occurred", e);
        return Result.error(ErrorCode.BASE_ERROR.getCode(), ErrorCode.BASE_ERROR.getMessage());
    }
} 