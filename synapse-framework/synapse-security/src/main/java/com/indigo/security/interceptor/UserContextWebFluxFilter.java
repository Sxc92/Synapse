package com.indigo.security.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.indigo.cache.session.UserSessionService;
import com.indigo.core.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 用户上下文WebFlux过滤器
 * 用于在每个请求开始时设置用户上下文到响应式上下文
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class UserContextWebFluxFilter implements WebFilter {

    private final UserSessionService userSessionService;

    public UserContextWebFluxFilter(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        try {
            // 从请求头获取 token
            String token = extractToken(exchange);
            
            if (StringUtils.hasText(token)) {
                // 从 Redis 中获取用户上下文
                UserContext userContext = userSessionService.getUserSession(token);
                
                if (userContext != null) {
                    // 设置到响应式上下文
                    return chain.filter(exchange)
                            .contextWrite(context -> context.put(UserContext.class, userContext))
                            .doFinally(signalType -> {
                                // 清理上下文，防止内存泄漏
                                UserContext.clearCurrentUser();
                            });
                } else {
                    log.warn("未找到用户上下文，token: {}", token);
                }
            } else {
                log.debug("请求中未包含 token，URL: {}", exchange.getRequest().getURI());
            }
        } catch (Exception e) {
            log.error("设置用户上下文时发生异常", e);
        }
        
        return chain.filter(exchange);
    }

    /**
     * 从请求头中提取 token
     */
    private String extractToken(ServerWebExchange exchange) {
        // 优先从 Authorization 头获取
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // 从自定义头获取
        String token = exchange.getRequest().getHeaders().getFirst("X-Auth-Token");
        if (StringUtils.hasText(token)) {
            return token;
        }
        
        // 从参数获取
        token = exchange.getRequest().getQueryParams().getFirst("token");
        if (StringUtils.hasText(token)) {
            return token;
        }
        
        return null;
    }
} 