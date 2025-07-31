package com.indigo.events.core;

/**
 * 事件状态枚举
 * 定义事件在生命周期中的各种状态
 *
 * @author 史偕成
 * @date 2025/01/08
 */
public enum EventStatus {
    
    /**
     * 待处理 - 事件已创建，等待处理
     */
    PENDING("待处理"),
    
    /**
     * 处理中 - 事件正在被处理
     */
    PROCESSING("处理中"),
    
    /**
     * 成功 - 事件处理成功
     */
    SUCCESS("成功"),
    
    /**
     * 失败 - 事件处理失败
     */
    FAILED("失败"),
    
    /**
     * 重试中 - 事件正在重试
     */
    RETRYING("重试中"),
    
    /**
     * 已取消 - 事件已被取消
     */
    CANCELLED("已取消"),
    
    /**
     * 已回滚 - 事件已被回滚
     */
    ROLLBACK("已回滚");
    
    private final String description;
    
    EventStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否为终态
     */
    public boolean isFinal() {
        return this == SUCCESS || this == FAILED || this == CANCELLED || this == ROLLBACK;
    }
    
    /**
     * 检查是否可以重试
     */
    public boolean canRetry() {
        return this == FAILED || this == RETRYING;
    }
    
    /**
     * 检查是否可以回滚
     */
    public boolean canRollback() {
        return this == SUCCESS || this == PROCESSING;
    }
} 