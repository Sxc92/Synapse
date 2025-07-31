package com.indigo.core.exception;

import com.indigo.core.exception.enums.ErrorCode;
import com.indigo.core.utils.MessageUtils;

import java.util.Locale;

/**
 * Exception with internationalization support
 * 
 * @author 史偕成
 * @date 2025/04/24 21:57
 **/
public class I18nException extends BaseException {
    
    private final Locale locale;
    
    public I18nException(String code, Locale locale, Object... args) {
        super(code, MessageUtils.getMessage(code, args, locale), args);
        this.locale = locale;
    }
    
    public I18nException(String code, String fallbackMessage, Locale locale, Object... args) {
        super(code, fallbackMessage, args);
        this.locale = locale;
    }
    
    public I18nException(String code, Locale locale, Throwable cause, Object... args) {
        super(code, MessageUtils.getMessage(code, args, locale), cause, args);
        this.locale = locale;
    }
    
    public I18nException(ErrorCode errorCode, Locale locale, Object... args) {
        super(errorCode.getCode(), MessageUtils.getMessage(errorCode.getMessage(), args, locale), args);
        this.locale = locale;
    }
    
    public I18nException(ErrorCode errorCode, Locale locale, Throwable cause, Object... args) {
        super(errorCode.getCode(), MessageUtils.getMessage(errorCode.getMessage(), args, locale), cause, args);
        this.locale = locale;
    }
    
    /**
     * Get the locale used for this exception
     * 
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }
    
    /**
     * Factory method to create an I18nException with the system default locale
     * 
     * @param errorCode the error code
     * @param args message arguments
     * @return a new I18nException
     */
    public static I18nException of(ErrorCode errorCode, Object... args) {
        return new I18nException(errorCode, Locale.getDefault(), args);
    }
    
    /**
     * Factory method to create an I18nException with the system default locale
     * 
     * @param errorCode the error code
     * @param cause the cause of the exception
     * @param args message arguments
     * @return a new I18nException
     */
    public static I18nException of(ErrorCode errorCode, Throwable cause, Object... args) {
        return new I18nException(errorCode, Locale.getDefault(), cause, args);
    }
} 