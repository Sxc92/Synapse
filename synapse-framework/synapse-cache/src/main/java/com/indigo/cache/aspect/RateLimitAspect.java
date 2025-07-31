package com.indigo.cache.aspect;

import com.indigo.cache.annotation.RateLimit;
import com.indigo.cache.extension.RateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * WebMVC 限流切面
 * 拦截 @RateLimit 注解，执行限流逻辑
 * 仅在 WebMVC 环境下启用（需要 HttpServletRequest 支持）
 * 
 * 注意：在 WebFlux 环境中，请使用 ReactiveRateLimitAspect
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@Component
@ConditionalOnClass(HttpServletRequest.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class RateLimitAspect extends AbstractRateLimitAspect {

    public RateLimitAspect(RateLimitService rateLimitService) {
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
        
        // 添加请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            context.setVariable("request", request);
            context.setVariable("response", attributes.getResponse());
        }
        
        // 添加方法信息
        context.setVariable("methodName", joinPoint.getSignature().getName());
        context.setVariable("className", joinPoint.getTarget().getClass().getSimpleName());
        
        return context;
    }

    @Override
    protected String getDefaultKey(ProceedingJoinPoint joinPoint) {
        StringBuilder key = new StringBuilder();
        
        // 添加 IP 地址
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String ip = getClientIp(request);
            key.append(ip).append(":");
        }
        
        // 添加方法签名
        key.append(joinPoint.getSignature().getDeclaringType().getSimpleName())
           .append(".")
           .append(joinPoint.getSignature().getName());
        
        return key.toString();
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 