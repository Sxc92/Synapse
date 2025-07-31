package com.indigo.events.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 事件处理器注册表
 * 管理所有事件处理器的注册和查找
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Slf4j
@Component
public class EventHandlerRegistry {
    
    private final Map<String, List<EventHandler>> handlersByEventType = new ConcurrentHashMap<>();
    private final Map<String, EventHandler> handlersByName = new ConcurrentHashMap<>();
    
    @Autowired(required = false)
    private List<EventHandler> eventHandlers = new ArrayList<>();
    
    @PostConstruct
    public void init() {
        registerHandlers();
    }
    
    /**
     * 注册所有事件处理器
     */
    private void registerHandlers() {
        if (eventHandlers.isEmpty()) {
            log.warn("No event handlers found");
            return;
        }
        
        for (EventHandler handler : eventHandlers) {
            registerHandler(handler);
        }
        
        log.info("Registered {} event handlers", eventHandlers.size());
        logRegisteredHandlers();
    }
    
    /**
     * 注册单个事件处理器
     */
    public void registerHandler(EventHandler handler) {
        if (handler == null) {
            log.warn("Attempted to register null handler");
            return;
        }
        
        String eventType = handler.getEventType();
        if (eventType == null || eventType.trim().isEmpty()) {
            log.warn("Handler {} has null or empty event type", handler.getName());
            return;
        }
        
        // 注册到事件类型映射
        handlersByEventType.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
        
        // 注册到名称映射
        handlersByName.put(handler.getName(), handler);
        
        log.debug("Registered handler: {} for event type: {}", handler.getName(), eventType);
    }
    
    /**
     * 注销事件处理器
     */
    public void unregisterHandler(EventHandler handler) {
        if (handler == null) {
            return;
        }
        
        String eventType = handler.getEventType();
        if (eventType != null) {
            List<EventHandler> handlers = handlersByEventType.get(eventType);
            if (handlers != null) {
                handlers.remove(handler);
                if (handlers.isEmpty()) {
                    handlersByEventType.remove(eventType);
                }
            }
        }
        
        handlersByName.remove(handler.getName());
        
        log.debug("Unregistered handler: {} for event type: {}", handler.getName(), eventType);
    }
    
    /**
     * 获取指定事件类型的所有处理器
     */
    public List<EventHandler> getHandlers(String eventType) {
        if (eventType == null) {
            return Collections.emptyList();
        }
        
        List<EventHandler> handlers = handlersByEventType.get(eventType);
        if (handlers == null) {
            return Collections.emptyList();
        }
        
        // 按优先级排序
        return handlers.stream()
                .sorted(Comparator.comparingInt(EventHandler::getPriority).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * 获取指定名称的处理器
     */
    public EventHandler getHandler(String name) {
        return handlersByName.get(name);
    }
    
    /**
     * 获取所有已注册的事件类型
     */
    public Set<String> getRegisteredEventTypes() {
        return new HashSet<>(handlersByEventType.keySet());
    }
    
    /**
     * 获取所有已注册的处理器
     */
    public List<EventHandler> getAllHandlers() {
        return new ArrayList<>(handlersByName.values());
    }
    
    /**
     * 检查是否有处理器支持指定事件类型
     */
    public boolean hasHandlers(String eventType) {
        List<EventHandler> handlers = handlersByEventType.get(eventType);
        return handlers != null && !handlers.isEmpty();
    }
    
    /**
     * 获取处理器统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalHandlers", handlersByName.size());
        stats.put("eventTypes", handlersByEventType.size());
        stats.put("registeredEventTypes", getRegisteredEventTypes());
        stats.put("handlerNames", new ArrayList<>(handlersByName.keySet()));
        
        // 每个事件类型的处理器数量
        Map<String, Integer> handlersPerEventType = new HashMap<>();
        handlersByEventType.forEach((eventType, handlers) -> 
            handlersPerEventType.put(eventType, handlers.size()));
        stats.put("handlersPerEventType", handlersPerEventType);
        
        return stats;
    }
    
    /**
     * 记录已注册的处理器
     */
    private void logRegisteredHandlers() {
        if (log.isDebugEnabled()) {
            log.debug("Registered handlers:");
            handlersByEventType.forEach((eventType, handlers) -> {
                log.debug("  Event type: {} -> {} handlers", eventType, handlers.size());
                handlers.forEach(handler -> 
                    log.debug("    - {} (priority: {})", handler.getName(), handler.getPriority()));
            });
        }
    }
    
    /**
     * 清空所有注册的处理器
     */
    public void clear() {
        handlersByEventType.clear();
        handlersByName.clear();
        log.info("Cleared all event handlers");
    }
} 