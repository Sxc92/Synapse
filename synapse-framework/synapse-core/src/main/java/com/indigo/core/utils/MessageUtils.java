package com.indigo.core.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 国际化消息工具类
 * 
 * @author 史偕成
 * @date 2024/03/21
 **/
@Component
public class MessageUtils {
    
    private static MessageSource messageSource;
    
    public MessageUtils(MessageSource messageSource) {
        MessageUtils.messageSource = messageSource;
    }
    
    /**
     * 获取国际化消息
     *
     * @param code 消息代码
     * @return 国际化消息
     */
    public static String getMessage(String code) {
        return getMessage(code, (Object[]) null);
    }
    
    /**
     * 获取国际化消息
     *
     * @param code 消息代码
     * @param args 参数
     * @return 国际化消息
     */
    public static String getMessage(String code, Object... args) {
        return getMessage(code, args, code);
    }
    
    /**
     * 获取国际化消息
     *
     * @param code 消息代码
     * @param args 参数
     * @param defaultMessage 默认消息
     * @return 国际化消息
     */
    public static String getMessage(String code, Object[] args, String defaultMessage) {
        if (messageSource == null) {
            return defaultMessage;
        }
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }
    
    /**
     * 获取国际化消息
     *
     * @param code 消息代码
     * @param locale 语言环境
     * @return 国际化消息
     */
    public static String getMessage(String code, Locale locale) {
        return getMessage(code, (Object[]) null, code, locale);
    }
    
    /**
     * 获取国际化消息
     *
     * @param code 消息代码
     * @param args 参数
     * @param locale 语言环境
     * @return 国际化消息
     */
    public static String getMessage(String code, Object[] args, Locale locale) {
        return getMessage(code, args, code, locale);
    }
    
    /**
     * 获取国际化消息
     *
     * @param code 消息代码
     * @param args 参数
     * @param defaultMessage 默认消息
     * @param locale 语言环境
     * @return 国际化消息
     */
    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        if (messageSource == null) {
            return defaultMessage;
        }
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }
} 