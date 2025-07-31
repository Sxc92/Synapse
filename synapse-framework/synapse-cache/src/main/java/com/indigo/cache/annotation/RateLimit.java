package com.indigo.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * 用于标记需要进行限流的方法
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * 限流键，支持SpEL表达式
     */
    String key() default "";
    
    /**
     * 限流算法类型
     */
    String algorithm() default "SLIDING_WINDOW";
    
    /**
     * 时间窗口（秒）
     */
    int window() default 60;
    
    /**
     * 最大请求数
     */
    int limit() default 100;
    
    /**
     * 限流策略
     */
    String strategy() default "REJECT";
    
    /**
     * 限流提示信息
     */
    String message() default "请求过于频繁，请稍后再试";
} 