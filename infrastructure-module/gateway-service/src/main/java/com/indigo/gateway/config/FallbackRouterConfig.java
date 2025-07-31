package com.indigo.gateway.config;

import com.indigo.gateway.handler.FallbackHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;

/**
 * 回退路由配置
 * 配置服务不可用时的回退处理
 *
 * @author 史偕成
 * @date 2025/01/07
 */
@Configuration
public class FallbackRouterConfig {

    /**
     * 配置回退路由
     */
    @Bean
    public RouterFunction<ServerResponse> fallbackRouter(FallbackHandler fallbackHandler) {
        return RouterFunctions.route()
                // 通用回退路由
                .route(path("/fallback"), fallbackHandler::fallback)
                // 认证服务回退路由
                .route(path("/fallback/auth"), fallbackHandler::authFallback)
                // 业务服务回退路由
                .route(path("/fallback/business"), fallbackHandler::businessFallback)
                // 第三方服务回退路由
                .route(path("/fallback/third-party"), fallbackHandler::thirdPartyFallback)
                .build();
    }
} 