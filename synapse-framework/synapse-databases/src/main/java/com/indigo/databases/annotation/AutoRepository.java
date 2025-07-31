package com.indigo.databases.annotation;

import java.lang.annotation.*;

/**
 * 自动Repository注解
 * 标记需要自动生成实现的Repository接口
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoRepository {
    
    /**
     * Repository名称
     */
    String value() default "";
    
    /**
     * 是否启用缓存
     */
    boolean enableCache() default false;
    
    /**
     * 缓存前缀
     */
    String cachePrefix() default "";
} 