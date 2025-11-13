package com.indigo.gateway.config;

import com.indigo.security.config.SecurityProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Gateway 核心配置类
 * 专注于：
 * 1. JWT token传递
 * 2. 权限认证（内部用户和第三方平台）
 * 3. 网络限流
 * 
 * 模块职责划分：
 * - synapse-core: 多语言国际化统一处理
 * - synapse-cache: 缓存处理，包含用户会话获取
 * - synapse-security: 权限处理
 * - gateway: JWT token传递、权限认证、网络限流
 *
 * @author 史偕成
 * @date 2025/01/07
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "synapse.gateway")
@EnableConfigurationProperties(SecurityProperties.class)
public class GatewayConfig {

    /**
     * 提供 SecurityProperties Bean（如果未自动配置）
     * 确保 Gateway 可以使用 SecurityProperties 的白名单配置
     */
//    @Bean
//    @ConditionalOnMissingBean(SecurityProperties.class)
//    public SecurityProperties securityProperties() {
//        log.info("SecurityProperties 未自动配置，创建默认实例");
//        return new SecurityProperties();
//    }


    /**
     * 第三方平台配置
     */
    private ThirdPartyConfig thirdParty = new ThirdPartyConfig();

    /**
     * 限流配置
     */
    private RateLimitConfig rateLimit = new RateLimitConfig();

    /**
     * 语言环境配置
     */
    private LocaleConfig locale = new LocaleConfig();


    @Data
    public static class ThirdPartyConfig {
        /**
         * 是否启用第三方平台认证
         */
        private boolean enabled = true;
        
        /**
         * 第三方平台认证路径
         */
        private List<String> paths = List.of("/api/third-party/**");
        
        /**
         * 第三方平台API Key header名称
         */
        private String apiKeyHeader = "X-API-Key";
        
        /**
         * 第三方平台认证超时时间（秒）
         */
        private int timeout = 30;
    }

    @Data
    public static class RateLimitConfig {
        /**
         * 是否启用限流
         */
        private boolean enabled = true;
        
        /**
         * 默认限流次数
         */
        private int defaultLimit = 100;
        
        /**
         * 限流时间窗口（秒）
         */
        private int windowSeconds = 60;
        
        /**
         * IP限流配置
         */
        private IpRateLimit ipRateLimit = new IpRateLimit();
        
        /**
         * 用户限流配置
         */
        private UserRateLimit userRateLimit = new UserRateLimit();
    }

    @Data
    public static class IpRateLimit {
        /**
         * 是否启用IP限流
         */
        private boolean enabled = true;
        
        /**
         * IP限流次数
         */
        private int limit = 200;
        
        /**
         * 时间窗口（秒）
         */
        private int windowSeconds = 60;
    }

    @Data
    public static class UserRateLimit {
        /**
         * 是否启用用户限流
         */
        private boolean enabled = true;
        
        /**
         * 用户限流次数
         */
        private int limit = 1000;
        
        /**
         * 时间窗口（秒）
         */
        private int windowSeconds = 60;
    }

    @Data
    public static class LocaleConfig {
        /**
         * 是否启用语言环境处理
         */
        private boolean enabled = true;
        
        /**
         * 默认语言环境
         */
        private String defaultLocale = "zh_CN";
        
        /**
         * 语言环境参数名（URL参数）
         */
        private String paramName = "lang";
        
        /**
         * 是否记录语言环境日志
         */
        private boolean logLocale = false;
        
        /**
         * 语言环境请求头名称
         */
        private String headerName = "X-Locale";
        
        /**
         * 是否严格验证语言环境格式
         * true: 严格按照ISO标准验证
         * false: 宽松验证，允许更多格式
         */
        private boolean strictValidation = true;
        
        /**
         * 是否允许未知语言环境
         * true: 允许任何有效格式的语言环境
         * false: 只允许常见的语言环境
         */
        private boolean allowUnknownLocales = true;
    }

} 