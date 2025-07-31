package com.indigo.events.core;

import java.util.concurrent.CompletableFuture;

/**
 * 事件消费者接口
 * 定义事件消费的基本方法
 *
 * @author 史偕成
 * @date 2025/01/08
 */
public interface EventConsumer {
    
    /**
     * 消费事件
     *
     * @param event 要消费的事件
     * @return 消费结果
     */
    EventResult consume(Event event);
    
    /**
     * 异步消费事件
     *
     * @param event 要消费的事件
     * @return 异步消费结果
     */
    CompletableFuture<EventResult> consumeAsync(Event event);
    
    /**
     * 批量消费事件
     *
     * @param events 事件列表
     * @return 批量消费结果
     */
    EventResult consumeBatch(java.util.List<Event> events);
    
    /**
     * 启动消费者
     */
    void start();
    
    /**
     * 停止消费者
     */
    void stop();
    
    /**
     * 检查消费者是否正在运行
     *
     * @return 是否正在运行
     */
    boolean isRunning();
    
    /**
     * 获取消费者状态
     *
     * @return 消费者状态
     */
    ConsumerStatus getStatus();
    
    /**
     * 获取消费者统计信息
     *
     * @return 统计信息
     */
    ConsumerStats getStats();
} 