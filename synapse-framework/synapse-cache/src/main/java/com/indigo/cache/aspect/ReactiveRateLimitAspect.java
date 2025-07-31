package com.indigo.cache.aspect;

import com.indigo.cache.annotation.RateLimit;
import com.indigo.cache.extension.RateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

/**
 * 响应式限流切面
 * 拦截 @RateLimit 注解，执行限流逻辑
 * 仅在 WebFlux 环境下启用（支持 ServerHttpRequest）
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveRateLimitAspect extends AbstractRateLimitAspect {

    public ReactiveRateLimitAspect(RateLimitService rateLimitService) {
        super(rateLimitService);
    }

    @Override
    protected EvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // 添加方法参数
        Object[] args = joinPoint.getArgs();
        String[] paramNames = getParameterNames(joinPoint);
        for (int i = 0; i < args.length; i++) {
            if (paramNames != null && i < paramNames.length) {
                context.setVariable(paramNames[i], args[i]);
            }
            context.setVariable("p" + i, args[i]);
        }
        
        // 尝试从参数中获取 ServerWebExchange
        Optional<ServerWebExchange> exchangeOpt = findServerWebExchange(args);
        if (exchangeOpt.isPresent()) {
            ServerWebExchange exchange = exchangeOpt.get();
            ServerHttpRequest request = exchange.getRequest();
            
            context.setVariable("request", request);
            context.setVariable("exchange", exchange);
            
            // 添加请求信息
            context.setVariable("uri", request.getURI().toString());
            context.setVariable("method", request.getMethod().name());
            context.setVariable("path", request.getPath().toString());
        }
        
        // 添加方法信息
        context.setVariable("methodName", joinPoint.getSignature().getName());
        context.setVariable("className", joinPoint.getTarget().getClass().getSimpleName());
        
        return context;
    }

    @Override
    protected String getDefaultKey(ProceedingJoinPoint joinPoint) {
        StringBuilder key = new StringBuilder();
        
        // 尝试添加 IP 地址
        Object[] args = joinPoint.getArgs();
        Optional<ServerWebExchange> exchangeOpt = findServerWebExchange(args);
        
        if (exchangeOpt.isPresent()) {
            ServerHttpRequest request = exchangeOpt.get().getRequest();
            String ip = getClientIp(request);
            key.append(ip).append(":");
        } else {
            // 如果没有找到 ServerWebExchange，使用默认前缀
            key.append("unknown-ip:");
        }
        
        // 添加方法签名
        key.append(joinPoint.getSignature().getDeclaringType().getSimpleName())
           .append(".")
           .append(joinPoint.getSignature().getName());
        
        return key.toString();
    }

    /**
     * 从参数中查找 ServerWebExchange
     */
    private Optional<ServerWebExchange> findServerWebExchange(Object[] args) {
        if (args == null) return Optional.empty();
        
        for (Object arg : args) {
            if (arg instanceof ServerWebExchange) {
                return Optional.of((ServerWebExchange) arg);
            }
        }
        return Optional.empty();
    }

    /**
     * 获取客户端 IP (WebFlux 版本)
     */
    private String getClientIp(ServerHttpRequest request) {
        // 检查各种可能的 IP 头
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 从 remoteAddress 获取
            if (request.getRemoteAddress() != null) {
                ip = request.getRemoteAddress().getAddress().getHostAddress();
            }
        }
        
        return ip != null ? ip : "unknown";
    }


} 