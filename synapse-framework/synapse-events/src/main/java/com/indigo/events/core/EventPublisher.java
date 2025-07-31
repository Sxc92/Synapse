package com.indigo.events.core;

import java.util.concurrent.CompletableFuture;

/**
 * 事件发布器接口
 * 定义事件发布的基本方法
 *
 * @author 史偕成
 * @date 2025/01/08
 */
public interface EventPublisher {
    
    /**
     * 发布事件
     *
     * @param event 要发布的事件
     * @return 发布结果
     */
    PublishResult publish(Event event);
    
    /**
     * 异步发布事件
     *
     * @param event 要发布的事件
     * @return 异步发布结果
     */
    CompletableFuture<PublishResult> publishAsync(Event event);
    
    /**
     * 发布事务事件
     *
     * @param transactionId 事务ID
     * @param eventType 事件类型
     * @param sourceService 源服务
     * @param eventData 事件数据
     * @return 发布结果
     */
    default PublishResult publishTransactionEvent(String transactionId, String eventType, 
                                                 String sourceService, java.util.Map<String, Object> eventData) {
        Event event = Event.create(transactionId, eventType, sourceService, eventData);
        return publish(event);
    }
    
    /**
     * 发布高优先级事件
     *
     * @param event 要发布的事件
     * @return 发布结果
     */
    default PublishResult publishUrgent(Event event) {
        event.setPriority(EventPriority.URGENT);
        return publish(event);
    }
    
    /**
     * 发布系统事件
     *
     * @param event 要发布的事件
     * @return 发布结果
     */
    default PublishResult publishSystem(Event event) {
        event.setPriority(EventPriority.SYSTEM);
        return publish(event);
    }
} 