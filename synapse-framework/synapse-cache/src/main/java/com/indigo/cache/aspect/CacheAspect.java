package com.indigo.cache.aspect;

import com.indigo.cache.annotation.CacheEvict;
import com.indigo.cache.annotation.Cacheable;
import com.indigo.cache.core.TwoLevelCacheService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 缓存切面，处理缓存注解
 *
 * @author 史偕成
 * @date 2025/05/16 10:20
 */
@Aspect
@Component
public class CacheAspect {

    private final TwoLevelCacheService cacheService;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public CacheAspect(TwoLevelCacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * 处理Cacheable注解
     */
    @Around("@annotation(com.indigo.cache.annotation.Cacheable)")
    public Object cacheableAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        // 获取模块名和键值
        String module = getModule(cacheable.module(), method);
        String spelKey = cacheable.key();
        long expireSeconds = cacheable.expireSeconds();
        TwoLevelCacheService.CacheStrategy strategy = cacheable.strategy();
        
        // 解析SpEL表达式的缓存键
        String key = parseKey(spelKey, method, joinPoint.getArgs());
        
        try {
            // 从缓存获取数据
            Optional<?> cachedResult = cacheService.get(module, key, strategy);
            
            // 缓存命中，直接返回
            if (cachedResult.isPresent()) {
                return cachedResult.get();
            }
            
            // 调用原方法
            Object result = joinPoint.proceed();
            
            // 检查缓存条件
            if (shouldCache(cacheable.condition(), result, method, joinPoint.getArgs())) {
                // 将结果存入缓存
                if (result != null) {
                    cacheService.save(module, key, result, expireSeconds, strategy);
                }
            }
            
            return result;
        } catch (Exception e) {
            if (cacheable.disableOnException()) {
                // 异常发生时禁用缓存，直接调用原方法
                return joinPoint.proceed();
            }
            throw e;
        }
    }

    /**
     * 处理CacheEvict注解
     */
    @Around("@annotation(com.indigo.cache.annotation.CacheEvict)")
    public Object cacheEvictAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);

        // 获取模块名和键值
        String module = getModule(cacheEvict.module(), method);
        String spelKey = cacheEvict.key();
        TwoLevelCacheService.CacheStrategy strategy = cacheEvict.strategy();
        boolean allEntries = cacheEvict.allEntries();
        boolean beforeInvocation = cacheEvict.beforeInvocation();
        
        // 解析SpEL表达式的缓存键
        String key = parseKey(spelKey, method, joinPoint.getArgs());
        
        try {
            // 如果是在方法执行前清除缓存
            if (beforeInvocation) {
                evictCache(module, key, allEntries, strategy);
            }
            
            // 调用原方法
            Object result = joinPoint.proceed();
            
            // 如果是在方法执行后清除缓存
            if (!beforeInvocation) {
                // 检查清除条件
                if (shouldEvict(cacheEvict.condition(), result, method, joinPoint.getArgs())) {
                    evictCache(module, key, allEntries, strategy);
                }
            }
            
            return result;
        } catch (Exception e) {
            if (cacheEvict.disableOnException()) {
                // 异常发生时禁用缓存操作，直接调用原方法
                return joinPoint.proceed();
            }
            throw e;
        }
    }
    
    /**
     * 清除缓存
     */
    private void evictCache(String module, String key, boolean allEntries, TwoLevelCacheService.CacheStrategy strategy) {
        if (allEntries) {
            // 清除模块下所有缓存
            // 这里可以根据实际情况实现清除模块所有缓存的逻辑
        } else {
            // 清除指定键的缓存
            cacheService.delete(module, key, strategy);
        }
    }
    
    /**
     * 获取模块名，如果注解中未指定，则使用方法所在类的简单名称
     */
    private String getModule(String annotationModule, Method method) {
        if (StringUtils.hasText(annotationModule)) {
            return annotationModule;
        }
        return method.getDeclaringClass().getSimpleName();
    }
    
    /**
     * 解析SpEL表达式的缓存键
     */
    private String parseKey(String spelKey, Method method, Object[] args) {
        if (!spelKey.contains("#")) {
            // 不包含SpEL表达式，直接返回
            return spelKey;
        }
        
        EvaluationContext context = createEvaluationContext(method, args);
        Expression expression = expressionParser.parseExpression(spelKey);
        return expression.getValue(context, String.class);
    }
    
    /**
     * 创建SpEL表达式的评估上下文
     */
    private EvaluationContext createEvaluationContext(Method method, Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // 获取方法参数名
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        if (parameterNames != null && args != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        
        return context;
    }
    
    /**
     * 判断是否应该缓存结果
     */
    private boolean shouldCache(String condition, Object result, Method method, Object[] args) {
        if (!StringUtils.hasText(condition)) {
            return true;
        }
        
        EvaluationContext context = createEvaluationContext(method, args);
        context.setVariable("result", result);
        Expression expression = expressionParser.parseExpression(condition);
        return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
    }
    
    /**
     * 判断是否应该清除缓存
     */
    private boolean shouldEvict(String condition, Object result, Method method, Object[] args) {
        if (!StringUtils.hasText(condition)) {
            return true;
        }
        
        EvaluationContext context = createEvaluationContext(method, args);
        context.setVariable("result", result);
        Expression expression = expressionParser.parseExpression(condition);
        return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
    }
} 