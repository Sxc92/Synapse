package com.indigo.core.exception;

/**
 * 安全异常
 * 用于处理安全相关的异常情况
 *
 * @author 史偕成
 * @date 2024/01/09
 */
public class SecurityException extends BaseException {

    /**
     * 构造函数
     *
     * @param message 异常消息
     */
    public SecurityException(String message) {
        super("SECURITY_ERROR", message);
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause   原因异常
     */
    public SecurityException(String message, Throwable cause) {
        super("SECURITY_ERROR", message, cause);
    }

    /**
     * 构造函数
     *
     * @param code    错误代码
     * @param message 异常消息
     */
    public SecurityException(String code, String message) {
        super(code, message);
    }

    /**
     * 构造函数
     *
     * @param code    错误代码
     * @param message 异常消息
     * @param cause   原因异常
     */
    public SecurityException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
} 