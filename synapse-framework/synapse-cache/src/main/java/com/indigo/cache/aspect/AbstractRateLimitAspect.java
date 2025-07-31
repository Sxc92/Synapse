package com.indigo.cache.aspect;

import com.indigo.cache.annotation.RateLimit;
import com.indigo.cache.extension.RateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * 限流切面抽象基类
 * 提取公共的限流逻辑，减少代码重复
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@Aspect
@Order(100)
@Component
public abstract class AbstractRateLimitAspect {

    protected final RateLimitService rateLimitService;
    protected final ExpressionParser parser = new SpelExpressionParser();

    protected AbstractRateLimitAspect(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 生成限流键
        String key = generateKey(joinPoint, rateLimit);
        
        // 转换时间单位（简化处理，直接使用秒）
        long timeWindow = rateLimit.window();
        
        // 执行限流检查
        boolean allowed = rateLimitService.isAllowed(key, rateLimit.algorithm(), timeWindow, rateLimit.limit());
        
        if (!allowed) {
            log.warn("请求被限流: key={}, algorithm={}, timeWindow={}s, maxRequests={}", 
                key, rateLimit.algorithm(), timeWindow, rateLimit.limit());
            
            // 根据策略处理限流
            return handleRateLimit(rateLimit);
        }
        
        // 限流通过，继续执行原方法
        return joinPoint.proceed();
    }

    /**
     * 生成限流键
     */
    protected String generateKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        String keyExpression = rateLimit.key();
        
        if (keyExpression.isEmpty()) {
            // 如果没有指定 key 表达式，使用默认的 IP + 方法名
            return getDefaultKey(joinPoint);
        }
        
        try {
            // 解析 SpEL 表达式
            Expression expression = parser.parseExpression(keyExpression);
            EvaluationContext context = createEvaluationContext(joinPoint);
            Object result = expression.getValue(context);
            return result != null ? result.toString() : getDefaultKey(joinPoint);
        } catch (Exception e) {
            log.warn("解析限流键表达式失败: {}", keyExpression, e);
            return getDefaultKey(joinPoint);
        }
    }

    /**
     * 创建表达式上下文 - 由子类实现
     */
    protected abstract EvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint);

    /**
     * 获取默认限流键 - 由子类实现
     */
    protected abstract String getDefaultKey(ProceedingJoinPoint joinPoint);

    /**
     * 获取参数名（简化实现）
     */
    protected String[] getParameterNames(ProceedingJoinPoint joinPoint) {
        // 这里可以使用反射或其他方式获取参数名
        // 简化实现，返回 null
        return null;
    }

    /**
     * 处理限流
     */
    protected Object handleRateLimit(RateLimit rateLimit) throws Throwable {
        return switch (rateLimit.strategy()) {
            case "REJECT" -> throw new RuntimeException(rateLimit.message());
            case "WAIT" -> {
                // 等待一段时间后重试（简化实现）
                Thread.sleep(1000);
                throw new RuntimeException(rateLimit.message());
            }
            case "FALLBACK" -> {
                // 返回默认值或执行降级逻辑
                log.warn("执行降级逻辑: {}", rateLimit.message());
                yield null;
            }
            default -> throw new RuntimeException(rateLimit.message());
        };
    }
} 