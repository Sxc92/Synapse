package com.indigo.gateway.filter;

import com.indigo.gateway.config.GatewayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Locale;

/**
 * 语言环境过滤器
 * 处理客户端请求的语言环境信息，并传递给下游服务
 * 支持动态语言环境，无需硬编码支持列表
 * 
 * @author 史偕成
 * @date 2025/01/27
 */
@Slf4j
@Component
public class LocaleFilter implements GlobalFilter, Ordered {
    
    @Autowired
    private GatewayConfig gatewayConfig;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 检查是否启用语言环境处理
        if (!gatewayConfig.getLocale().isEnabled()) {
            return chain.filter(exchange);
        }
        
        try {
            ServerHttpRequest request = exchange.getRequest();
            
            // 获取语言环境
            Locale locale = extractLocale(request);
            
            // 验证语言环境是否有效（不检查支持列表，支持所有有效格式）
            if (!isValidLocale(locale)) {
                locale = getDefaultLocale();
                if (gatewayConfig.getLocale().isLogLocale()) {
                    log.debug("Invalid locale detected, using default: {}", locale);
                }
            }
            
            // 添加语言环境到请求头，传递给下游服务
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Locale", locale.toString())
                    .header("X-Language", locale.getLanguage())
                    .build();
            
            // 记录语言环境信息
            if (gatewayConfig.getLocale().isLogLocale()) {
                log.debug("Locale detected: {} for request: {} {}", 
                         locale, request.getMethod(), request.getURI().getPath());
            }
            
            // 替换请求
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();
            
            return chain.filter(modifiedExchange);
            
        } catch (Exception e) {
            log.warn("Error processing locale in Gateway filter", e);
            // 即使出错也继续处理，使用默认语言环境
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-Locale", getDefaultLocale().toString())
                    .header("X-Language", getDefaultLocale().getLanguage())
                    .build();
            
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();
            
            return chain.filter(modifiedExchange);
        }
    }
    
    /**
     * 从请求中提取语言环境
     * 优先级：URL参数 > 自定义请求头 > Accept-Language头 > 默认语言环境
     * 
     * @param request 请求对象
     * @return 语言环境
     */
    private Locale extractLocale(ServerHttpRequest request) {
        GatewayConfig.LocaleConfig localeConfig = gatewayConfig.getLocale();
        
        // 1. 从URL参数获取（最高优先级）
        String localeParam = request.getQueryParams().getFirst(localeConfig.getParamName());
        if (localeParam != null && !localeParam.isEmpty()) {
            Locale locale = parseLocale(localeParam);
            if (locale != null) {
                return locale;
            }
        }
        
        // 2. 从自定义请求头获取
        String localeHeader = request.getHeaders().getFirst(localeConfig.getHeaderName());
        if (localeHeader != null && !localeHeader.isEmpty()) {
            Locale locale = parseLocale(localeHeader);
            if (locale != null) {
                return locale;
            }
        }
        
        // 3. 从Accept-Language头获取
        String acceptLanguage = request.getHeaders().getFirst("Accept-Language");
        if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
            Locale locale = parseAcceptLanguage(acceptLanguage);
            if (locale != null) {
                return locale;
            }
        }
        
        // 4. 使用默认语言环境
        return getDefaultLocale();
    }
    
    /**
     * 获取默认语言环境
     * 
     * @return 默认语言环境
     */
    private Locale getDefaultLocale() {
        String defaultLocaleStr = gatewayConfig.getLocale().getDefaultLocale();
        Locale defaultLocale = parseLocale(defaultLocaleStr);
        return defaultLocale != null ? defaultLocale : Locale.SIMPLIFIED_CHINESE;
    }
    
    /**
     * 解析语言环境字符串
     * 支持格式：zh_CN, zh-CN, zh, en_US, en-US, en
     * 
     * @param localeStr 语言环境字符串
     * @return 语言环境对象
     */
    private Locale parseLocale(String localeStr) {
        if (localeStr == null || localeStr.isEmpty()) {
            return null;
        }
        
        try {
            // 处理下划线和连字符
            String normalized = localeStr.replace('-', '_');
            String[] parts = normalized.split("_");
            
            if (parts.length == 1) {
                // 只有语言代码，如：zh, en
                return new Locale(parts[0]);
            } else if (parts.length == 2) {
                // 语言代码和国家代码，如：zh_CN, en_US
                return new Locale(parts[0], parts[1]);
            } else if (parts.length == 3) {
                // 语言代码、国家代码和变体，如：zh_CN_Hans
                return new Locale(parts[0], parts[1], parts[2]);
            }
            
            return null;
            
        } catch (Exception e) {
            log.debug("Failed to parse locale string: {}", localeStr, e);
            return null;
        }
    }
    
    /**
     * 解析Accept-Language头
     * 示例：zh-CN,zh;q=0.9,en;q=0.8
     * 
     * @param acceptLanguage Accept-Language头值
     * @return 语言环境对象
     */
    private Locale parseAcceptLanguage(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isEmpty()) {
            return null;
        }
        
        try {
            // 分割语言偏好，取第一个
            String[] languages = acceptLanguage.split(",");
            if (languages.length > 0) {
                // 移除质量值（q=0.9）
                String primaryLanguage = languages[0].split(";")[0].trim();
                return parseLocale(primaryLanguage);
            }
            
            return null;
            
        } catch (Exception e) {
            log.debug("Failed to parse Accept-Language: {}", acceptLanguage, e);
            return null;
        }
    }
    
    /**
     * 检查语言环境是否有效
     * 只检查格式是否正确，不限制支持的语言列表
     * 
     * @param locale 语言环境
     * @return 是否有效
     */
    private boolean isValidLocale(Locale locale) {
        if (locale == null) {
            return false;
        }
        
        // 检查语言代码是否为空
        String language = locale.getLanguage();
        if (language == null || language.isEmpty()) {
            return false;
        }
        
        // 检查语言代码长度（ISO 639标准：2-3个字符）
        if (language.length() < 2 || language.length() > 3) {
            return false;
        }
        
        // 检查国家代码（如果存在）
        String country = locale.getCountry();
        if (country != null && !country.isEmpty()) {
            // ISO 3166标准：2个字符
            if (country.length() != 2) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public int getOrder() {
        // 设置优先级，在日志过滤器之前执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
