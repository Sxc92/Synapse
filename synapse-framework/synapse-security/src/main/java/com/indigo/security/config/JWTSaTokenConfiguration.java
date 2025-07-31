package com.indigo.security.config;

import com.indigo.security.core.JWTStpLogic;
import com.indigo.security.interceptor.UserContextInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * JWT Sa-Token 配置类
 * 基于Sa-Token框架的JWT配置
 * 
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnMissingClass("org.springframework.web.reactive.config.WebFluxConfigurer")
@ConditionalOnMissingBean(name = "jwtSaTokenConfigurationEmpty")
@ConditionalOnBean(UserContextInterceptor.class)
public class JWTSaTokenConfiguration implements WebMvcConfigurer {

    private final JWTStpLogic jwtStpLogic;
    private final UserContextInterceptor userContextInterceptor;

    public JWTSaTokenConfiguration(JWTStpLogic jwtStpLogic, UserContextInterceptor userContextInterceptor) {
        this.jwtStpLogic = jwtStpLogic;
        this.userContextInterceptor = userContextInterceptor;
        log.info("JWTSaTokenConfiguration 已加载 - 基于Sa-Token框架，支持JWT token生成");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册用户上下文拦截器，优先级设置为最高
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/logout", "/error", "/static/**", "/public/**")
                .order(1);
        
        log.info("用户上下文拦截器已注册");
    }
} 