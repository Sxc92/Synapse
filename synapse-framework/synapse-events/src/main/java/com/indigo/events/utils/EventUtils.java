package com.indigo.events.utils;

import com.indigo.events.core.Event;
import com.indigo.events.core.EventPriority;
import com.indigo.events.core.EventStatus;
import com.indigo.events.transaction.TransactionStatus;
import com.indigo.events.transaction.TransactionState;

import java.util.Map;
import java.util.UUID;

/**
 * 事件工具类
 * 提供事件相关的工具方法
 *
 * @author 史偕成
 * @date 2025/01/08
 */
public class EventUtils {
    
    /**
     * 生成事务ID
     *
     * @return 事务ID
     */
    public static String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 生成事件ID
     *
     * @return 事件ID
     */
    public static String generateEventId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 创建简单事件
     *
     * @param transactionId 事务ID
     * @param eventType 事件类型
     * @param sourceService 源服务
     * @param eventData 事件数据
     * @return 事件实例
     */
    public static Event createSimpleEvent(String transactionId, String eventType, 
                                        String sourceService, Map<String, Object> eventData) {
        return Event.create(transactionId, eventType, sourceService, eventData);
    }
    
    /**
     * 创建高优先级事件
     *
     * @param transactionId 事务ID
     * @param eventType 事件类型
     * @param sourceService 源服务
     * @param eventData 事件数据
     * @return 事件实例
     */
    public static Event createUrgentEvent(String transactionId, String eventType, 
                                        String sourceService, Map<String, Object> eventData) {
        Event event = Event.create(transactionId, eventType, sourceService, eventData);
        event.setPriority(EventPriority.URGENT);
        return event;
    }
    
    /**
     * 创建系统事件
     *
     * @param transactionId 事务ID
     * @param eventType 事件类型
     * @param sourceService 源服务
     * @param eventData 事件数据
     * @return 事件实例
     */
    public static Event createSystemEvent(String transactionId, String eventType, 
                                        String sourceService, Map<String, Object> eventData) {
        Event event = Event.create(transactionId, eventType, sourceService, eventData);
        event.setPriority(EventPriority.SYSTEM);
        return event;
    }
    
    /**
     * 检查事件是否有效
     *
     * @param event 事件
     * @return 是否有效
     */
    public static boolean isValidEvent(Event event) {
        return event != null && 
               event.getEventId() != null && 
               event.getTransactionId() != null && 
               event.getEventType() != null && 
               event.getSourceService() != null;
    }
    
    /**
     * 检查事件是否可以重试
     *
     * @param event 事件
     * @return 是否可以重试
     */
    public static boolean canRetry(Event event) {
        return event != null && 
               event.getStatus() != null && 
               event.getStatus().canRetry() && 
               event.canRetry();
    }
    
    /**
     * 检查事件是否可以回滚
     *
     * @param event 事件
     * @return 是否可以回滚
     */
    public static boolean canRollback(Event event) {
        return event != null && 
               event.getStatus() != null && 
               event.getStatus().canRollback();
    }
    
    /**
     * 创建事务状态
     *
     * @param transactionId 事务ID
     * @param businessType 业务类型
     * @param timeoutSeconds 超时时间（秒）
     * @return 事务状态
     */
    public static TransactionStatus createTransactionStatus(String transactionId, 
                                                          String businessType, 
                                                          int timeoutSeconds) {
        return TransactionStatus.create(transactionId, businessType, timeoutSeconds);
    }
    
    /**
     * 检查事务是否完成
     *
     * @param transactionStatus 事务状态
     * @return 是否完成
     */
    public static boolean isTransactionCompleted(TransactionStatus transactionStatus) {
        return transactionStatus != null && transactionStatus.isCompleted();
    }
    
    /**
     * 检查事务是否成功
     *
     * @param transactionStatus 事务状态
     * @return 是否成功
     */
    public static boolean isTransactionSuccess(TransactionStatus transactionStatus) {
        return transactionStatus != null && transactionStatus.isSuccess();
    }
    
    /**
     * 检查事务是否失败
     *
     * @param transactionStatus 事务状态
     * @return 是否失败
     */
    public static boolean isTransactionFailed(TransactionStatus transactionStatus) {
        return transactionStatus != null && transactionStatus.isFailed();
    }
    
    /**
     * 检查事务是否超时
     *
     * @param transactionStatus 事务状态
     * @return 是否超时
     */
    public static boolean isTransactionTimeout(TransactionStatus transactionStatus) {
        return transactionStatus != null && transactionStatus.isTimeout();
    }
} 