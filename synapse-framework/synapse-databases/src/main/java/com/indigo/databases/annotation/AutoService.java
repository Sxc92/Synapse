package com.indigo.databases.annotation;

import java.lang.annotation.*;

/**
 * 自动Service注解
 * 标记需要自动生成实现的Service接口
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoService {
    
    /**
     * Service名称
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