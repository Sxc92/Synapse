package com.indigo.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * 国际化配置
 * 注意：此配置仅在非 Gateway 服务中生效
 * 
 * @author 史偕成
 * @date 2024/03/21
 **/
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class I18nConfig {
    
    /**
     * 默认解析器，其中locale表示默认语言
     */
    @Bean
    @ConditionalOnMissingBean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return localeResolver;
    }
    
    /**
     * 配置消息源
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        // 指定国际化资源文件路径
        messageSource.setBasename("i18n/messages");
        // 指定默认编码
        messageSource.setDefaultEncoding("UTF-8");
        // 是否使用消息代码作为默认消息，而不是抛出NoSuchMessageException
        messageSource.setUseCodeAsDefaultMessage(true);
        // 设置缓存时间，-1表示永久缓存
        messageSource.setCacheSeconds(-1);
        return messageSource;
    }
    
    /**
     * 配置语言切换拦截器
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        // 设置语言参数名
        interceptor.setParamName("lang");
        return interceptor;
    }
} 