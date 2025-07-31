package com.indigo.events.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 事件处理结果
 * 定义事件处理的结果信息
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResult {
    
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
     * 处理时间
     */
    private LocalDateTime processTime;
    
    /**
     * 处理耗时（毫秒）
     */
    private Long processDuration;
    
    /**
     * 处理结果数据
     */
    private Map<String, Object> resultData;
    
    /**
     * 是否需要重试
     */
    private boolean needRetry;
    
    /**
     * 重试延迟（毫秒）
     */
    private Long retryDelay;
    
    /**
     * 创建成功结果
     */
    public static EventResult success(String eventId, String transactionId) {
        return EventResult.builder()
                .success(true)
                .eventId(eventId)
                .transactionId(transactionId)
                .processTime(LocalDateTime.now())
                .needRetry(false)
                .build();
    }
    
    /**
     * 创建成功结果（带数据）
     */
    public static EventResult success(String eventId, String transactionId, Map<String, Object> resultData) {
        return EventResult.builder()
                .success(true)
                .eventId(eventId)
                .transactionId(transactionId)
                .resultData(resultData)
                .processTime(LocalDateTime.now())
                .needRetry(false)
                .build();
    }
    
    /**
     * 创建失败结果
     */
    public static EventResult failure(String eventId, String transactionId, String errorCode, String errorMessage) {
        return EventResult.builder()
                .success(false)
                .eventId(eventId)
                .transactionId(transactionId)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .processTime(LocalDateTime.now())
                .needRetry(true)
                .retryDelay(5000L) // 默认5秒后重试
                .build();
    }
    
    /**
     * 创建失败结果（不重试）
     */
    public static EventResult failureNoRetry(String eventId, String transactionId, String errorCode, String errorMessage) {
        return EventResult.builder()
                .success(false)
                .eventId(eventId)
                .transactionId(transactionId)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .processTime(LocalDateTime.now())
                .needRetry(false)
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
    
    /**
     * 设置处理耗时
     */
    public void setProcessDuration(Long startTime) {
        if (startTime != null) {
            this.processDuration = System.currentTimeMillis() - startTime;
        }
    }
} 