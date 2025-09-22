package com.indigo.gateway.controller;

import com.indigo.gateway.config.GatewayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于验证网关配置和调试
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    private final GatewayConfig gatewayConfig;

    public TestController(GatewayConfig gatewayConfig) {
        this.gatewayConfig = gatewayConfig;
    }

    /**
     * 获取白名单配置
     */
    @GetMapping("/whitelist")
    public Map<String, Object> getWhitelist() {
        Map<String, Object> result = new HashMap<>();
        result.put("whiteList", gatewayConfig.getWhiteList());
        result.put("thirdParty", gatewayConfig.getThirdParty());
        result.put("tokenRenewal", gatewayConfig.getTokenRenewal());
        result.put("rateLimit", gatewayConfig.getRateLimit());
        result.put("locale", gatewayConfig.getLocale());
        
        log.info("白名单配置: {}", gatewayConfig.getWhiteList());
        return result;
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 语言环境测试
     * 测试Gateway语言环境处理功能
     */
    @GetMapping("/locale")
    public Map<String, Object> testLocale(
            @RequestParam(required = false) String lang,
            @RequestHeader(value = "X-Locale", required = false) String xLocale,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 配置信息
        result.put("config", Map.of(
            "enabled", gatewayConfig.getLocale().isEnabled(),
            "defaultLocale", gatewayConfig.getLocale().getDefaultLocale(),
            "paramName", gatewayConfig.getLocale().getParamName(),
            "headerName", gatewayConfig.getLocale().getHeaderName(),
            "strictValidation", gatewayConfig.getLocale().isStrictValidation(),
            "allowUnknownLocales", gatewayConfig.getLocale().isAllowUnknownLocales()
        ));
        
        // 请求信息
        result.put("request", Map.of(
            "lang", lang,
            "xLocale", xLocale,
            "acceptLanguage", acceptLanguage
        ));
        
        // 处理后的语言环境（模拟Gateway处理）
        String detectedLocale = detectLocale(lang, xLocale, acceptLanguage);
        result.put("detectedLocale", detectedLocale);
        
        log.info("语言环境测试 - URL参数: {}, X-Locale: {}, Accept-Language: {}, 检测结果: {}", 
                lang, xLocale, acceptLanguage, detectedLocale);
        
        return result;
    }
    
    /**
     * 模拟Gateway语言环境检测逻辑
     */
    private String detectLocale(String lang, String xLocale, String acceptLanguage) {
        // 1. URL参数优先级最高
        if (lang != null && !lang.isEmpty()) {
            return lang;
        }
        
        // 2. X-Locale请求头
        if (xLocale != null && !xLocale.isEmpty()) {
            return xLocale;
        }
        
        // 3. Accept-Language请求头
        if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
            // 取第一个语言偏好
            String[] languages = acceptLanguage.split(",");
            if (languages.length > 0) {
                return languages[0].split(";")[0].trim();
            }
        }
        
        // 4. 使用默认语言环境
        return gatewayConfig.getLocale().getDefaultLocale();
    }
} 