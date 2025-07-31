package com.indigo.databases.annotation;

import java.lang.annotation.*;

/**
 * Lambda查询注解
 * 用于支持MyBatis-Plus的Lambda QueryWrapper查询
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LambdaQuery {
    
    /**
     * 是否启用分页
     */
    boolean enablePage() default false;
    
    /**
     * 默认排序字段
     */
    String defaultOrderBy() default "";
    
    /**
     * 默认排序方向 (ASC/DESC)
     */
    String defaultOrderDirection() default "DESC";
    
    /**
     * 是否使用缓存
     */
    boolean useCache() default false;
    
    /**
     * 缓存时间（秒）
     */
    int cacheTimeout() default 300;
    
    /**
     * 超时时间（秒）
     */
    int timeout() default 30;
} 