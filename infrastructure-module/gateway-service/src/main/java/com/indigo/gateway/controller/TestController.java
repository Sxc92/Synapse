package com.indigo.gateway.controller;

import com.indigo.gateway.config.GatewayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
} 