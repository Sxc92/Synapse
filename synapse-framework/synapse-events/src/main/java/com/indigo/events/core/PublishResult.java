package com.indigo.events.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 发布结果
 * 定义事件发布的结果信息
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishResult {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 事件ID
     */
    private String eventId;
    
    /**
     * 事务ID
     */
    private String transactionId;
    
    /**
     * 错误码
     */
    private String errorCode;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    
    /**
     * 消息ID（RocketMQ）
     */
    private String messageId;
    
    /**
     * 创建成功结果
     */
    public static PublishResult success(String eventId, String transactionId, String messageId) {
        return PublishResult.builder()
                .success(true)
                .eventId(eventId)
                .transactionId(transactionId)
                .messageId(messageId)
                .publishTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 创建失败结果
     */
    public static PublishResult failure(String eventId, String transactionId, String errorCode, String errorMessage) {
        return PublishResult.builder()
                .success(false)
                .eventId(eventId)
                .transactionId(transactionId)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .publishTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 检查是否成功
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * 检查是否失败
     */
    public boolean isFailure() {
        return !success;
    }
} 