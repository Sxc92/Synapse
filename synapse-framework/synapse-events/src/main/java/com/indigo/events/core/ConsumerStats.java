package com.indigo.events.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 消费者统计信息
 * 记录事件消费者的基础运行统计
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumerStats {
    
    /**
     * 总消费事件数
     */
    private long totalConsumed;
    
    /**
     * 成功消费事件数
     */
    private long successConsumed;
    
    /**
     * 失败消费事件数
     */
    private long failedConsumed;
    
    /**
     * 重试事件数
     */
    private long retryCount;
    
    /**
     * 平均处理时间（毫秒）
     */
    private double averageProcessTime;
    
    /**
     * 最大处理时间（毫秒）
     */
    private long maxProcessTime;
    
    /**
     * 最小处理时间（毫秒）
     */
    private long minProcessTime;
    
    /**
     * 启动时间
     */
    private LocalDateTime startTime;
    
    /**
     * 最后处理时间
     */
    private LocalDateTime lastProcessTime;
    
    /**
     * 错误次数
     */
    private long errorCount;
    
    /**
     * 最后错误时间
     */
    private LocalDateTime lastErrorTime;
    
    /**
     * 最后错误信息
     */
    private String lastErrorMessage;
    
    /**
     * 创建初始统计信息
     */
    public static ConsumerStats create() {
        return ConsumerStats.builder()
                .totalConsumed(0)
                .successConsumed(0)
                .failedConsumed(0)
                .retryCount(0)
                .averageProcessTime(0.0)
                .maxProcessTime(0)
                .minProcessTime(Long.MAX_VALUE)
                .startTime(LocalDateTime.now())
                .errorCount(0)
                .build();
    }
    
    /**
     * 记录成功消费
     */
    public void recordSuccess(long processTime) {
        this.totalConsumed++;
        this.successConsumed++;
        this.lastProcessTime = LocalDateTime.now();
        updateProcessTimeStats(processTime);
    }
    
    /**
     * 记录失败消费
     */
    public void recordFailure(long processTime, String errorMessage) {
        this.totalConsumed++;
        this.failedConsumed++;
        this.errorCount++;
        this.lastProcessTime = LocalDateTime.now();
        this.lastErrorTime = LocalDateTime.now();
        this.lastErrorMessage = errorMessage;
        updateProcessTimeStats(processTime);
    }
    
    /**
     * 记录重试
     */
    public void recordRetry() {
        this.retryCount++;
    }
    
    /**
     * 更新处理时间统计
     */
    private void updateProcessTimeStats(long processTime) {
        if (processTime > this.maxProcessTime) {
            this.maxProcessTime = processTime;
        }
        if (processTime < this.minProcessTime) {
            this.minProcessTime = processTime;
        }
        
        // 计算平均处理时间
        if (this.totalConsumed > 0) {
            this.averageProcessTime = ((this.averageProcessTime * (this.totalConsumed - 1)) + processTime) / this.totalConsumed;
        }
    }
    
    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        return this.totalConsumed > 0 ? (double) this.successConsumed / this.totalConsumed : 0.0;
    }
    
    /**
     * 获取失败率
     */
    public double getFailureRate() {
        return this.totalConsumed > 0 ? (double) this.failedConsumed / this.totalConsumed : 0.0;
    }
    
    /**
     * 获取运行时间（秒）
     */
    public long getRunningTimeSeconds() {
        if (this.startTime == null) {
            return 0;
        }
        return java.time.Duration.between(this.startTime, LocalDateTime.now()).getSeconds();
    }
    
    /**
     * 获取处理速率（事件/秒）
     */
    public double getProcessRate() {
        long runningTime = getRunningTimeSeconds();
        return runningTime > 0 ? (double) this.totalConsumed / runningTime : 0.0;
    }
} 