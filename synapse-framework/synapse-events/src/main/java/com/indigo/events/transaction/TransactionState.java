package com.indigo.events.transaction;

/**
 * 事务状态枚举
 * 定义分布式事务的各种状态
 *
 * @author 史偕成
 * @date 2025/01/08
 */
public enum TransactionState {
    
    /**
     * 活跃 - 事务正在进行中
     */
    ACTIVE("活跃"),
    
    /**
     * 成功 - 事务执行成功
     */
    SUCCESS("成功"),
    
    /**
     * 失败 - 事务执行失败
     */
    FAILED("失败"),
    
    /**
     * 回滚中 - 事务正在回滚
     */
    ROLLBACKING("回滚中"),
    
    /**
     * 已回滚 - 事务已回滚
     */
    ROLLBACK("已回滚"),
    
    /**
     * 超时 - 事务超时
     */
    TIMEOUT("超时"),
    
    /**
     * 已取消 - 事务已取消
     */
    CANCELLED("已取消");
    
    private final String description;
    
    TransactionState(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否为终态
     */
    public boolean isFinal() {
        return this == SUCCESS || this == FAILED || this == ROLLBACK || 
               this == TIMEOUT || this == CANCELLED;
    }
    
    /**
     * 检查是否可以回滚
     */
    public boolean canRollback() {
        return this == ACTIVE || this == SUCCESS || this == FAILED;
    }
    
    /**
     * 检查是否为成功状态
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }
    
    /**
     * 检查是否为失败状态
     */
    public boolean isFailure() {
        return this == FAILED || this == TIMEOUT || this == CANCELLED;
    }
} 