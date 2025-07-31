package com.indigo.events.annotation;

import com.indigo.events.core.EventPriority;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 事件监听器注解
 * 用于标记事件处理方法
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface EventListener {
    
    /**
     * 事件类型，支持通配符匹配
     * 例如：user.*, *.created, user.created
     */
    String eventType() default "*";
    
    /**
     * 事件优先级过滤
     * 只处理指定优先级及以上的事件
     */
    EventPriority minPriority() default EventPriority.LOW;
    
    /**
     * 源服务过滤
     * 只处理来自指定服务的事件
     */
    String sourceService() default "*";
    
    /**
     * 是否异步处理
     */
    boolean async() default false;
    
    /**
     * 异步处理时的线程池名称
     */
    String executor() default "";
    
    /**
     * 重试次数
     */
    int retryCount() default 3;
    
    /**
     * 重试间隔（毫秒）
     */
    long retryInterval() default 1000;
    
    /**
     * 是否启用幂等性检查
     */
    boolean idempotent() default true;
    
    /**
     * 超时时间（毫秒）
     */
    long timeout() default 30000;
} 