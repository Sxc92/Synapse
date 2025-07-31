package com.indigo.events.transaction;

import java.util.List;

/**
 * 事务仓库接口
 * 定义事务数据的持久化操作
 *
 * @author 史偕成
 * @date 2025/01/08
 */
public interface TransactionRepository {
    
    /**
     * 保存事务
     *
     * @param transaction 事务状态
     */
    void saveTransaction(TransactionStatus transaction);
    
    /**
     * 更新事务
     *
     * @param transaction 事务状态
     */
    void updateTransaction(TransactionStatus transaction);
    
    /**
     * 根据事务ID查找事务
     *
     * @param transactionId 事务ID
     * @return 事务状态
     */
    TransactionStatus findByTransactionId(String transactionId);
    
    /**
     * 根据状态查找事务列表
     *
     * @param state 事务状态
     * @return 事务列表
     */
    List<TransactionStatus> findByState(TransactionState state);
    
    /**
     * 根据业务类型查找事务列表
     *
     * @param businessType 业务类型
     * @return 事务列表
     */
    List<TransactionStatus> findByBusinessType(String businessType);
    
    /**
     * 查找超时事务
     *
     * @return 超时事务列表
     */
    List<TransactionStatus> findTimeoutTransactions();
    
    /**
     * 删除事务
     *
     * @param transactionId 事务ID
     */
    void deleteTransaction(String transactionId);
    
    /**
     * 清理过期事务
     *
     * @param days 过期天数
     */
    void cleanupExpiredTransactions(int days);
    
    /**
     * 获取事务统计信息
     *
     * @return 统计信息
     */
    TransactionStats getTransactionStats();
} 