package com.indigo.events.transaction;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 事务统计信息
 * 记录事务处理的统计信息
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionStats {
    
    /**
     * 总事务数
     */
    private long totalTransactions;
    
    /**
     * 活跃事务数
     */
    private long activeTransactions;
    
    /**
     * 成功事务数
     */
    private long successTransactions;
    
    /**
     * 失败事务数
     */
    private long failedTransactions;
    
    /**
     * 超时事务数
     */
    private long timeoutTransactions;
    
    /**
     * 回滚事务数
     */
    private long rollbackTransactions;
    
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
     * 统计开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 统计结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 按业务类型统计
     */
    private Map<String, BusinessTypeStats> businessTypeStats;
    
    /**
     * 按状态统计
     */
    private Map<TransactionState, Long> stateStats;
    
    /**
     * 创建初始统计信息
     */
    public static TransactionStats create() {
        return TransactionStats.builder()
                .totalTransactions(0)
                .activeTransactions(0)
                .successTransactions(0)
                .failedTransactions(0)
                .timeoutTransactions(0)
                .rollbackTransactions(0)
                .averageProcessTime(0.0)
                .maxProcessTime(0)
                .minProcessTime(Long.MAX_VALUE)
                .startTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        return this.totalTransactions > 0 ? (double) this.successTransactions / this.totalTransactions : 0.0;
    }
    
    /**
     * 获取失败率
     */
    public double getFailureRate() {
        return this.totalTransactions > 0 ? (double) this.failedTransactions / this.totalTransactions : 0.0;
    }
    
    /**
     * 获取超时率
     */
    public double getTimeoutRate() {
        return this.totalTransactions > 0 ? (double) this.timeoutTransactions / this.totalTransactions : 0.0;
    }
    
    /**
     * 获取回滚率
     */
    public double getRollbackRate() {
        return this.totalTransactions > 0 ? (double) this.rollbackTransactions / this.totalTransactions : 0.0;
    }
    
    /**
     * 获取统计时间范围（秒）
     */
    public long getStatsTimeRangeSeconds() {
        if (this.startTime == null || this.endTime == null) {
            return 0;
        }
        return java.time.Duration.between(this.startTime, this.endTime).getSeconds();
    }
    
    /**
     * 获取事务处理速率（事务/秒）
     */
    public double getTransactionRate() {
        long timeRange = getStatsTimeRangeSeconds();
        return timeRange > 0 ? (double) this.totalTransactions / timeRange : 0.0;
    }
    
    /**
     * 业务类型统计信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BusinessTypeStats {
        
        /**
         * 业务类型
         */
        private String businessType;
        
        /**
         * 总事务数
         */
        private long totalTransactions;
        
        /**
         * 成功事务数
         */
        private long successTransactions;
        
        /**
         * 失败事务数
         */
        private long failedTransactions;
        
        /**
         * 平均处理时间（毫秒）
         */
        private double averageProcessTime;
        
        /**
         * 获取成功率
         */
        public double getSuccessRate() {
            return this.totalTransactions > 0 ? (double) this.successTransactions / this.totalTransactions : 0.0;
        }
        
        /**
         * 获取失败率
         */
        public double getFailureRate() {
            return this.totalTransactions > 0 ? (double) this.failedTransactions / this.totalTransactions : 0.0;
        }
    }
} 