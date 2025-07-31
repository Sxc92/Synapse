package com.indigo.security.config;

import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.stp.StpInterface;
import com.indigo.cache.core.CacheService;
import com.indigo.cache.session.UserSessionService;
import com.indigo.security.core.*;
import com.indigo.security.factory.AuthenticationStrategyFactory;
import com.indigo.security.interceptor.UserContextInterceptor;
import com.indigo.security.interceptor.UserContextWebFluxFilter;
import com.indigo.security.service.DefaultAuthenticationService;
import com.indigo.security.strategy.OAuth2AuthenticationStrategy;
import com.indigo.security.strategy.SaTokenAuthenticationStrategy;
import com.indigo.security.view.OAuth2ViewHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import jakarta.annotation.PostConstruct;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全配置自动装配类
 * 支持WebMVC和WebFlux环境
 *
 * @author 史偕成
 * @date 2024/01/08
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({UserSessionService.class})
@ComponentScan(basePackages = {
    "com.indigo.security.service",
    "com.indigo.security.factory",
    "com.indigo.security.core"
})
public class SecurityAutoConfiguration {

    /**
     * Token管理服务
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenManager tokenManager(UserSessionService userSessionService) {
        log.info("初始化Token管理服务");
        return new TokenManager(userSessionService);
    }

    /**
     * 权限管理服务
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(StpInterface.class)
    public PermissionManager permissionManager(UserSessionService userSessionService) {
        log.info("初始化权限管理服务");
        return new PermissionManager(userSessionService);
    }

    /**
     * 配置OAuth2
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "synapse.oauth2.enabled", havingValue = "true", matchIfMissing = false)
    public SaOAuth2Config oAuth2Config() {
        log.info("初始化OAuth2.0配置");
        SaOAuth2Config config = new SaOAuth2Config();
        // 基础的OAuth2配置，具体的视图处理可以在后续版本中完善
        return config;
    }

    /**
     * OAuth2.0模板
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SaOAuth2Config.class)
    public SaOAuth2Template saOAuth2Template() {
        log.info("初始化OAuth2.0模板");
        return new SaOAuth2Template();
    }

    /**
     * Sa-Token认证策略
     */
    @Bean
    @ConditionalOnMissingBean
    public SaTokenAuthenticationStrategy saTokenAuthenticationStrategy(
            TokenManager tokenManager,
            UserSessionService userSessionService) {
        log.info("初始化Sa-Token认证策略");
        return new SaTokenAuthenticationStrategy(tokenManager, userSessionService);
    }

    /**
     * OAuth2认证策略
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({SaOAuth2Config.class, SaOAuth2Template.class})
    public OAuth2AuthenticationStrategy oAuth2AuthenticationStrategy(
            TokenManager tokenManager,
            UserSessionService userSessionService) {
        log.info("初始化OAuth2.0认证策略");
        return new OAuth2AuthenticationStrategy(tokenManager, userSessionService);
    }

    /**
     * WebMVC环境配置
     */
    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean(type = "org.springframework.web.reactive.config.WebFluxConfigurer")
    public static class WebMvcSecurityConfiguration {
        
        // WebMVC相关配置
        @PostConstruct
        public void init() {
            log.info("WebMVC安全配置已加载");
        }

        /**
         * JWT认证逻辑
         */
        @Bean
        @ConditionalOnMissingBean
        public JWTStpLogic jwtStpLogic() {
            log.info("初始化JWT认证逻辑");
            return new JWTStpLogic();
        }

        @Bean
        public UserContextInterceptor userContextInterceptor(UserSessionService userSessionService) {
            log.info("创建用户上下文拦截器");
            return new UserContextInterceptor(userSessionService);
        }
        
        /**
         * JWT Sa-Token 配置Bean for WebMVC
         */
        @Bean
        @ConditionalOnMissingBean(name = "jwtSaTokenConfiguration")
        public WebMvcConfigurer jwtSaTokenConfiguration(JWTStpLogic jwtStpLogic, UserContextInterceptor userContextInterceptor) {
            log.info("创建JWT Sa-Token配置Bean for WebMVC");
            return new JWTSaTokenConfiguration(jwtStpLogic, userContextInterceptor);
        }
    }

    /**
     * WebFlux环境配置
     */
    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @ConditionalOnMissingBean(type = "org.springframework.web.servlet.config.annotation.WebMvcConfigurer")
    public static class WebFluxSecurityConfiguration implements WebFluxConfigurer {
        
        // WebFlux相关配置
        @PostConstruct
        public void init() {
            log.info("WebFlux安全配置已加载");
        }
        

    }
} 