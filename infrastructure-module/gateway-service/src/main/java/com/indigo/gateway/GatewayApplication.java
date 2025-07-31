package com.indigo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.config.LocalResponseCacheAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Gateway Service Application
 * 排除LocalResponseCacheAutoConfiguration避免cacheKeyGenerator Bean冲突
 * 
 * @author 史偕成
 * @date 2025/04/24 21:57
 **/
@SpringBootApplication(exclude = {LocalResponseCacheAutoConfiguration.class, JacksonAutoConfiguration.class})
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.indigo")
public class GatewayApplication {
    
    public static void main(String[] args) {
        // 已在application.yml中配置 spring.cloud.bootstrap.enabled=true
        SpringApplication.run(GatewayApplication.class, args);
    }
} 