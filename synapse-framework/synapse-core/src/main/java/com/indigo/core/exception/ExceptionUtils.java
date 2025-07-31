package com.indigo.core.exception;

import com.indigo.core.exception.enums.ErrorCode;

import java.util.Locale;

/**
 * Utility class for exception handling
 * 
 * @author 史偕成
 * @date 2025/04/24 21:57
 **/
public class ExceptionUtils {
    
    /**
     * Throw a business exception if the condition is true
     * 
     * @param condition the condition to check
     * @param errorCode the error code
     * @param args message arguments
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, Object... args) {
        if (condition) {
            throw new BusinessException(errorCode, args);
        }
    }
    
    /**
     * Throw a business exception if the object is null
     * 
     * @param object the object to check
     * @param errorCode the error code
     * @param args message arguments
     * @param <T> the type of the object
     * @return the object if not null
     */
    public static <T> T requireNonNull(T object, ErrorCode errorCode, Object... args) {
        if (object == null) {
            throw new BusinessException(errorCode, args);
        }
        return object;
    }
    
    /**
     * Throw an I18nException if the condition is true
     * 
     * @param condition the condition to check
     * @param errorCode the error code
     * @param locale the target locale
     * @param args message arguments
     */
    public static void throwI18nIf(boolean condition, ErrorCode errorCode, Locale locale, Object... args) {
        if (condition) {
            throw new I18nException(errorCode, locale, args);
        }
    }
    
    /**
     * Throw an I18nException with default locale if the condition is true
     * 
     * @param condition the condition to check
     * @param errorCode the error code
     * @param args message arguments
     */
    public static void throwI18nIf(boolean condition, ErrorCode errorCode, Object... args) {
        if (condition) {
            throw I18nException.of(errorCode, args);
        }
    }
    
    /**
     * Wraps a checked exception and throws an unchecked exception
     * 
     * @param exception the checked exception
     * @param errorCode the error code
     * @param args message arguments
     */
    public static void wrapAndThrow(Exception exception, ErrorCode errorCode, Object... args) {
        throw new IAMException(errorCode, exception, args);
    }
    
    /**
     * Provides a way to execute code and wrap any checked exceptions
     * 
     * @param execution the code to execute
     * @param errorCode the error code to use if an exception occurs
     * @param args message arguments
     * @param <T> the return type
     * @return the result of the execution
     */
    public static <T> T executeWithExceptionHandling(Execution<T> execution, ErrorCode errorCode, Object... args) {
        try {
            return execution.execute();
        } catch (Exception e) {
            wrapAndThrow(e, errorCode, args);
            return null; // never reached
        }
    }
    
    /**
     * Functional interface for code execution with exception handling
     * 
     * @param <T> the return type
     */
    @FunctionalInterface
    public interface Execution<T> {
        T execute() throws Exception;
    }
} 