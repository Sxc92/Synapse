package com.indigo.events.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 事务状态
 * 定义分布式事务的状态信息
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionStatus {
    
    /**
     * 事务ID
     */
    private String transactionId;
    
    /**
     * 业务类型
     */
    private String businessType;
    
    /**
     * 事务状态
     */
    private TransactionState state;
    
    /**
     * 总事件数
     */
    private int totalEvents;
    
    /**
     * 成功事件数
     */
    private int successEvents;
    
    /**
     * 失败事件数
     */
    private int failedEvents;
    
    /**
     * 待处理事件数
     */
    private int pendingEvents;
    
    /**
     * 事务创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    /**
     * 事务更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    
    /**
     * 事务超时时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeoutTime;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 事件ID列表
     */
    private List<String> eventIds;
    
    /**
     * 创建事务状态
     */
    public static TransactionStatus create(String transactionId, String businessType, int timeoutSeconds) {
        LocalDateTime now = LocalDateTime.now();
        return TransactionStatus.builder()
                .transactionId(transactionId)
                .businessType(businessType)
                .state(TransactionState.ACTIVE)
                .totalEvents(0)
                .successEvents(0)
                .failedEvents(0)
                .pendingEvents(0)
                .createdTime(now)
                .updatedTime(now)
                .timeoutTime(now.plusSeconds(timeoutSeconds))
                .build();
    }
    
    /**
     * 添加事件
     */
    public void addEvent(String eventId) {
        this.totalEvents++;
        this.pendingEvents++;
        this.updatedTime = LocalDateTime.now();
        if (this.eventIds != null) {
            this.eventIds.add(eventId);
        }
    }
    
    /**
     * 事件处理成功
     */
    public void eventSuccess() {
        this.successEvents++;
        this.pendingEvents--;
        this.updatedTime = LocalDateTime.now();
        checkCompletion();
    }
    
    /**
     * 事件处理失败
     */
    public void eventFailed() {
        this.failedEvents++;
        this.pendingEvents--;
        this.updatedTime = LocalDateTime.now();
        checkCompletion();
    }
    
    /**
     * 检查事务是否完成
     */
    private void checkCompletion() {
        if (this.pendingEvents == 0) {
            if (this.failedEvents == 0) {
                this.state = TransactionState.SUCCESS;
            } else {
                this.state = TransactionState.FAILED;
            }
        }
    }
    
    /**
     * 检查事务是否超时
     */
    public boolean isTimeout() {
        return LocalDateTime.now().isAfter(this.timeoutTime);
    }
    
    /**
     * 检查事务是否完成
     */
    public boolean isCompleted() {
        return this.state == TransactionState.SUCCESS || 
               this.state == TransactionState.FAILED || 
               this.state == TransactionState.ROLLBACK;
    }
    
    /**
     * 检查事务是否成功
     */
    public boolean isSuccess() {
        return this.state == TransactionState.SUCCESS;
    }
    
    /**
     * 检查事务是否失败
     */
    public boolean isFailed() {
        return this.state == TransactionState.FAILED;
    }
    
    /**
     * 设置错误信息
     */
    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
        this.updatedTime = LocalDateTime.now();
    }
} 