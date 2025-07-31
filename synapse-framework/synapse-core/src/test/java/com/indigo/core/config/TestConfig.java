package com.indigo.core.config;

import com.indigo.core.utils.MessageUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Test configuration for core module
 * 
 * @author 史偕成
 * @date 2024/03/21
 **/
@Configuration
public class TestConfig {
    
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public MessageUtils messageUtils(MessageSource messageSource) {
        return new MessageUtils(messageSource);
    }
} 