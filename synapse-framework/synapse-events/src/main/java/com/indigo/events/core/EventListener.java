package com.indigo.events.core;

/**
 * 事件监听器接口
 * 定义事件监听的基本方法
 *
 * @author 史偕成
 * @date 2025/01/08
 */
public interface EventListener {
    
    /**
     * 处理事件
     *
     * @param event 要处理的事件
     * @return 处理结果
     */
    EventResult handle(Event event);
    
    /**
     * 获取支持的事件类型
     *
     * @return 支持的事件类型数组
     */
    String[] getSupportedEventTypes();
    
    /**
     * 检查是否支持指定事件类型
     *
     * @param eventType 事件类型
     * @return 是否支持
     */
    default boolean supportsEventType(String eventType) {
        String[] supportedTypes = getSupportedEventTypes();
        if (supportedTypes == null || supportedTypes.length == 0) {
            return true; // 如果没有指定支持的类型，则支持所有类型
        }
        
        for (String supportedType : supportedTypes) {
            if (supportedType.equals(eventType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取监听器优先级
     * 数值越大，优先级越高
     *
     * @return 优先级
     */
    default int getPriority() {
        return 0;
    }
    
    /**
     * 检查是否为异步处理
     *
     * @return 是否异步
     */
    default boolean isAsync() {
        return false;
    }
    
    /**
     * 获取监听器名称
     *
     * @return 监听器名称
     */
    default String getListenerName() {
        return this.getClass().getSimpleName();
    }
} 