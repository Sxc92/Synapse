package com.indigo.events.core;

/**
 * 事件优先级枚举
 * 定义事件的处理优先级
 *
 * @author 史偕成
 * @date 2025/01/08
 */
public enum EventPriority {
    
    /**
     * 低优先级 - 可以延迟处理
     */
    LOW(1, "低优先级"),
    
    /**
     * 普通优先级 - 正常处理
     */
    NORMAL(2, "普通优先级"),
    
    /**
     * 高优先级 - 优先处理
     */
    HIGH(3, "高优先级"),
    
    /**
     * 紧急优先级 - 立即处理
     */
    URGENT(4, "紧急优先级"),
    
    /**
     * 系统优先级 - 系统级事件，最高优先级
     */
    SYSTEM(5, "系统优先级");
    
    private final int level;
    private final String description;
    
    EventPriority(int level, String description) {
        this.level = level;
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 比较优先级
     */
    public boolean isHigherThan(EventPriority other) {
        return this.level > other.level;
    }
    
    /**
     * 检查是否为系统级事件
     */
    public boolean isSystem() {
        return this == SYSTEM;
    }
    
    /**
     * 检查是否为紧急事件
     */
    public boolean isUrgent() {
        return this == URGENT || this == SYSTEM;
    }
} 