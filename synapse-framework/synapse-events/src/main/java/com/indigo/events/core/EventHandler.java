package com.indigo.events.core;

/**
 * 事件处理器接口
 * 定义事件处理的基本方法
 *
 * @author 史偕成
 * @date 2025/01/08
 */
public interface EventHandler {
    
    /**
     * 处理事件
     *
     * @param event 要处理的事件
     * @return 处理结果
     */
    EventResult handle(Event event);
    
    /**
     * 获取处理器支持的事件类型
     *
     * @return 事件类型
     */
    String getEventType();
    
    /**
     * 获取处理器优先级
     * 数值越大优先级越高
     *
     * @return 优先级
     */
    default int getPriority() {
        return 0;
    }
    
    /**
     * 检查是否支持该事件
     *
     * @param event 事件
     * @return 是否支持
     */
    default boolean supports(Event event) {
        return event != null && getEventType().equals(event.getEventType());
    }
    
    /**
     * 获取处理器名称
     *
     * @return 处理器名称
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
} 