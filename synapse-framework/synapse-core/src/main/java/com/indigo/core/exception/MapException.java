package com.indigo.core.exception;

/**
 * Map 操作异常
 * 
 * @author 史偕成
 * @date 2025/04/24 22:30
 **/
public class MapException extends RuntimeException {

    public MapException(String message) {
        super(message);
    }

    public MapException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapException(Throwable cause) {
        super(cause);
    }
} 