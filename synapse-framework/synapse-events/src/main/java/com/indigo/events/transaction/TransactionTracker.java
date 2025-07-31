package com.indigo.events.transaction;

import com.indigo.events.core.Event;
import com.indigo.events.core.EventStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 事务跟踪器
 * 跟踪和管理分布式事务的状态
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Slf4j
@Component
public class TransactionTracker {
    
    private final Map<String, TransactionStatus> transactionCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    public TransactionTracker() {
        // 启动定时任务，清理超时的事务
        scheduler.scheduleAtFixedRate(this::cleanupTimeoutTransactions, 1, 1, TimeUnit.MINUTES);
    }
    
    /**
     * 记录事务开始
     */
    public void recordTransactionStart(String transactionId, String businessType, int timeoutSeconds) {
        TransactionStatus status = TransactionStatus.create(transactionId, businessType, timeoutSeconds);
        transactionCache.put(transactionId, status);
        
        // 异步保存到数据库
        scheduler.submit(() -> {
            try {
                transactionRepository.saveTransaction(status);
                log.debug("Transaction started: {}", transactionId);
            } catch (Exception e) {
                log.error("Failed to save transaction: {}", transactionId, e);
            }
        });
    }
    
    /**
     * 添加事件到事务
     */
    public void addEventToTransaction(String transactionId, String eventId) {
        TransactionStatus status = transactionCache.get(transactionId);
        if (status != null) {
            status.addEvent(eventId);
            updateTransaction(transactionId, status);
        } else {
            log.warn("Transaction not found: {}", transactionId);
        }
    }
    
    /**
     * 记录事件状态
     */
    public void recordEventStatus(String transactionId, String eventId, EventStatus eventStatus) {
        TransactionStatus status = transactionCache.get(transactionId);
        if (status == null) {
            log.warn("Transaction not found: {}", transactionId);
            return;
        }
        
        switch (eventStatus) {
            case SUCCESS:
                status.eventSuccess();
                break;
            case FAILED:
                status.eventFailed();
                break;
            default:
                // 其他状态不更新事务统计
                break;
        }
        
        updateTransaction(transactionId, status);
        
        // 检查事务是否完成
        if (status.getState().isFinal()) {
            handleTransactionCompletion(transactionId, status);
        }
    }
    
    /**
     * 获取事务状态
     */
    public TransactionStatus getTransactionStatus(String transactionId) {
        TransactionStatus status = transactionCache.get(transactionId);
        if (status == null) {
            // 尝试从数据库加载
            status = transactionRepository.findByTransactionId(transactionId);
            if (status != null) {
                transactionCache.put(transactionId, status);
            }
        }
        return status;
    }
    
    /**
     * 更新事务状态
     */
    public void updateTransactionStatus(String transactionId, TransactionState state) {
        TransactionStatus status = transactionCache.get(transactionId);
        if (status != null) {
            status.setState(state);
            status.setUpdatedTime(LocalDateTime.now());
            updateTransaction(transactionId, status);
        }
    }
    
    /**
     * 设置事务错误
     */
    public void setTransactionError(String transactionId, String errorMessage) {
        TransactionStatus status = transactionCache.get(transactionId);
        if (status != null) {
            status.setErrorMessage(errorMessage);
            status.setUpdatedTime(LocalDateTime.now());
            updateTransaction(transactionId, status);
        }
    }
    
    /**
     * 检查事务是否超时
     */
    public boolean isTransactionTimeout(String transactionId) {
        TransactionStatus status = getTransactionStatus(transactionId);
        return status != null && status.isTimeout();
    }
    
    /**
     * 获取活跃事务列表
     */
    public List<TransactionStatus> getActiveTransactions() {
        return transactionCache.values().stream()
                .filter(status -> status.getState() == TransactionState.ACTIVE)
                .toList();
    }
    
    /**
     * 获取超时事务列表
     */
    public List<TransactionStatus> getTimeoutTransactions() {
        return transactionCache.values().stream()
                .filter(TransactionStatus::isTimeout)
                .toList();
    }
    
    /**
     * 清理事务缓存
     */
    public void cleanupTransaction(String transactionId) {
        transactionCache.remove(transactionId);
        log.debug("Cleaned up transaction cache: {}", transactionId);
    }
    
    /**
     * 获取事务统计信息
     */
    public Map<String, Object> getTransactionStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        long totalTransactions = transactionCache.size();
        long activeTransactions = transactionCache.values().stream()
                .filter(status -> status.getState() == TransactionState.ACTIVE)
                .count();
        long successTransactions = transactionCache.values().stream()
                .filter(status -> status.getState() == TransactionState.SUCCESS)
                .count();
        long failedTransactions = transactionCache.values().stream()
                .filter(status -> status.getState() == TransactionState.FAILED)
                .count();
        long timeoutTransactions = transactionCache.values().stream()
                .filter(TransactionStatus::isTimeout)
                .count();
        
        stats.put("totalTransactions", totalTransactions);
        stats.put("activeTransactions", activeTransactions);
        stats.put("successTransactions", successTransactions);
        stats.put("failedTransactions", failedTransactions);
        stats.put("timeoutTransactions", timeoutTransactions);
        
        if (totalTransactions > 0) {
            stats.put("successRate", (double) successTransactions / totalTransactions);
            stats.put("failureRate", (double) failedTransactions / totalTransactions);
        }
        
        return stats;
    }
    
    /**
     * 更新事务
     */
    private void updateTransaction(String transactionId, TransactionStatus status) {
        // 异步更新数据库
        scheduler.submit(() -> {
            try {
                transactionRepository.updateTransaction(status);
            } catch (Exception e) {
                log.error("Failed to update transaction: {}", transactionId, e);
            }
        });
    }
    
    /**
     * 处理事务完成
     */
    private void handleTransactionCompletion(String transactionId, TransactionStatus status) {
        log.info("Transaction completed: {} - {}", transactionId, status.getState());
        
        // 根据事务状态执行相应操作
        switch (status.getState()) {
            case SUCCESS:
                handleTransactionSuccess(transactionId, status);
                break;
            case FAILED:
                handleTransactionFailure(transactionId, status);
                break;
            case TIMEOUT:
                handleTransactionTimeout(transactionId, status);
                break;
        }
        
        // 延迟清理缓存
        scheduler.schedule(() -> cleanupTransaction(transactionId), 5, TimeUnit.MINUTES);
    }
    
    /**
     * 处理事务成功
     */
    private void handleTransactionSuccess(String transactionId, TransactionStatus status) {
        // 可以在这里添加成功后的处理逻辑
        // 例如：发送通知、更新统计等
        log.info("Transaction succeeded: {} - {} events processed", 
                transactionId, status.getSuccessEvents());
    }
    
    /**
     * 处理事务失败
     */
    private void handleTransactionFailure(String transactionId, TransactionStatus status) {
        // 可以在这里添加失败后的处理逻辑
        // 例如：触发回滚、发送告警等
        log.error("Transaction failed: {} - {} events failed", 
                transactionId, status.getFailedEvents());
    }
    
    /**
     * 处理事务超时
     */
    private void handleTransactionTimeout(String transactionId, TransactionStatus status) {
        // 可以在这里添加超时后的处理逻辑
        // 例如：触发回滚、发送告警等
        log.warn("Transaction timeout: {} - {} events pending", 
                transactionId, status.getPendingEvents());
    }
    
    /**
     * 清理超时事务
     */
    private void cleanupTimeoutTransactions() {
        try {
            List<TransactionStatus> timeoutTransactions = getTimeoutTransactions();
            for (TransactionStatus status : timeoutTransactions) {
                if (status.isTimeout()) {
                    status.setState(TransactionState.TIMEOUT);
                    handleTransactionTimeout(status.getTransactionId(), status);
                    updateTransaction(status.getTransactionId(), status);
                }
            }
        } catch (Exception e) {
            log.error("Error during timeout cleanup", e);
        }
    }
    
    /**
     * 关闭跟踪器
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
} 