package com.indigo.cache.annotation;

import com.indigo.cache.core.TwoLevelCacheService;

import java.lang.annotation.*;

/**
 * 缓存删除注解，用于方法级别的缓存删除配置
 * 配合TwoLevelCacheService使用
 *
 * @author 史偕成
 * @date 2025/05/16 10:15
 * @see TwoLevelCacheService.CacheStrategy
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {

    /**
     * 缓存模块名，用于区分不同业务领域的缓存
     */
    String module() default "";

    /**
     * 缓存键名，支持SpEL表达式，如：#user.id
     */
    String key();

    /**
     * 是否清除模块下的所有缓存
     */
    boolean allEntries() default false;

    /**
     * 缓存策略
     */
    TwoLevelCacheService.CacheStrategy strategy() default TwoLevelCacheService.CacheStrategy.LOCAL_AND_REDIS;

    /**
     * 是否在方法执行之前清除缓存
     */
    boolean beforeInvocation() default false;

    /**
     * 是否在发生异常时禁用缓存操作
     */
    boolean disableOnException() default true;
    
    /**
     * 删除条件，支持SpEL表达式，如：#result != null
     */
    String condition() default "";
} 