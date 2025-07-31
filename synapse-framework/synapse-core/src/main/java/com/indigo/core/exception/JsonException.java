package com.indigo.core.exception;

/**
 * JSON 处理异常
 * 支持国际化 messageKey 及参数
 *
 * @author 史偕成
 * @date 2024/05/14
 */
public class JsonException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final String messageKey;
    private final Object[] args;
    
    public JsonException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = null;
    }
    
    public JsonException(String messageKey, Throwable cause) {
        super(messageKey, cause);
        this.messageKey = messageKey;
        this.args = null;
    }
    
    public JsonException(String messageKey, Object[] args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }
    
    public JsonException(String messageKey, Object[] args, Throwable cause) {
        super(messageKey, cause);
        this.messageKey = messageKey;
        this.args = args;
    }
    
    public String getMessageKey() {
        return messageKey;
    }
    
    public Object[] getArgs() {
        return args;
    }
} 