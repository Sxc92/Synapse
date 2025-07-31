package com.indigo.events.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 事件基类
 * 定义事件的基本结构和属性
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    
    /**
     * 事件ID，全局唯一
     */
    private String eventId;
    
    /**
     * 事务ID，关联同一事务的所有事件
     */
    private String transactionId;
    
    /**
     * 事件类型，用于事件路由和处理
     */
    private String eventType;
    
    /**
     * 事件版本，用于兼容性处理
     */
    private String version;
    
    /**
     * 源服务名称
     */
    private String sourceService;
    
    /**
     * 目标服务名称（可选）
     */
    private String targetService;
    
    /**
     * 事件数据，JSON格式
     */
    private Map<String, Object> eventData;
    
    /**
     * 事件状态
     */
    private EventStatus status;
    
    /**
     * 事件优先级
     */
    private EventPriority priority;
    
    /**
     * 事件创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    /**
     * 事件更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建事件实例
     */
    public static Event create(String transactionId, String eventType, String sourceService, Map<String, Object> eventData) {
        return Event.builder()
                .eventId(UUID.randomUUID().toString())
                .transactionId(transactionId)
                .eventType(eventType)
                .version("1.0")
                .sourceService(sourceService)
                .eventData(eventData)
                .status(EventStatus.PENDING)
                .priority(EventPriority.NORMAL)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .retryCount(0)
                .maxRetryCount(3)
                .build();
    }
    
    /**
     * 更新事件状态
     */
    public void updateStatus(EventStatus status) {
        this.status = status;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount++;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 检查是否可以重试
     */
    public boolean canRetry() {
        return this.retryCount < this.maxRetryCount;
    }
    
    /**
     * 设置错误信息
     */
    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
        this.updatedTime = LocalDateTime.now();
    }
} 