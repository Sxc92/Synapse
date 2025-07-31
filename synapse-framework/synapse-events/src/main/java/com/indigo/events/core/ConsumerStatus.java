package com.indigo.events.core;

/**
 * 消费者状态枚举
 * 定义事件消费者的各种状态
 *
 * @author 史偕成
 * @date 2025/01/08
 */
public enum ConsumerStatus {
    
    /**
     * 初始化 - 消费者已创建但未启动
     */
    INITIALIZED("初始化"),
    
    /**
     * 启动中 - 消费者正在启动
     */
    STARTING("启动中"),
    
    /**
     * 运行中 - 消费者正在运行
     */
    RUNNING("运行中"),
    
    /**
     * 暂停 - 消费者已暂停
     */
    PAUSED("暂停"),
    
    /**
     * 停止中 - 消费者正在停止
     */
    STOPPING("停止中"),
    
    /**
     * 已停止 - 消费者已停止
     */
    STOPPED("已停止"),
    
    /**
     * 错误 - 消费者发生错误
     */
    ERROR("错误");
    
    private final String description;
    
    ConsumerStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否可以启动
     */
    public boolean canStart() {
        return this == INITIALIZED || this == STOPPED || this == ERROR;
    }
    
    /**
     * 检查是否可以停止
     */
    public boolean canStop() {
        return this == RUNNING || this == PAUSED;
    }
    
    /**
     * 检查是否正在运行
     */
    public boolean isRunning() {
        return this == RUNNING;
    }
    
    /**
     * 检查是否为终态
     */
    public boolean isFinal() {
        return this == STOPPED || this == ERROR;
    }
} 