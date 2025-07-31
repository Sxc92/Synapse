package com.indigo.databases.annotation;

import java.lang.annotation.*;

/**
 * SQL查询注解
 * 用于在接口方法上直接定义SQL查询语句
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlQuery {
    
    /**
     * SQL查询语句
     */
    String value();
    
    /**
     * 返回类型
     */
    Class<?> resultType() default Object.class;
    
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