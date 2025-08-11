//package com.indigo.iam.api.model.dto;
//
//import com.indigo.databases.annotation.QueryCondition;
//import com.indigo.databases.dto.PageDTO;
//import lombok.Builder;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
//import java.time.LocalDateTime;
//
///**
// * 租户查询条件对象
// * 提供更灵活的查询条件构建
// *
// * @author 史偕成
// * @date 2024/12/19
// */
//@Data
//@Builder
//@EqualsAndHashCode(callSuper = true)
//public class TenantQueryCondition extends PageDTO {
//
//    @QueryCondition(type = QueryCondition.QueryType.LIKE)
//    private String code;
//
//    @QueryCondition(type = QueryCondition.QueryType.LIKE)
//    private String description;
//
//    @QueryCondition(type = QueryCondition.QueryType.EQ)
//    private Integer status;
//
//    @QueryCondition(type = QueryCondition.QueryType.BETWEEN, field = "create_time")
//    private LocalDateTime[] createTime;
//
//    @QueryCondition(type = QueryCondition.QueryType.BETWEEN, field = "expire_time")
//    private LocalDateTime[] expireTime;
//
//    // ==================== 链式调用方法 ====================
//
//    /**
//     * 设置状态条件
//     */
//    public TenantQueryCondition status(Integer status) {
//        this.status = status;
//        return this;
//    }
//
//    /**
//     * 设置代码条件
//     */
//    public TenantQueryCondition code(String code) {
//        this.code = code;
//        return this;
//    }
//
//    /**
//     * 设置描述条件
//     */
//    public TenantQueryCondition description(String description) {
//        this.description = description;
//        return this;
//    }
//
//    /**
//     * 设置创建时间范围
//     */
//    public TenantQueryCondition createTimeRange(LocalDateTime start, LocalDateTime end) {
//        this.createTime = new LocalDateTime[]{start, end};
//        return this;
//    }
//
//    /**
//     * 设置过期时间范围
//     */
//    public TenantQueryCondition expireTimeRange(LocalDateTime start, LocalDateTime end) {
//        this.expireTime = new LocalDateTime[]{start, end};
//        return this;
//    }
//
//    /**
//     * 设置分页参数
//     */
//    public TenantQueryCondition page(int current, int size) {
//        this.setCurrent(current);
//        this.setSize(size);
//        return this;
//    }
//
//    /**
//     * 添加排序
//     */
//    public TenantQueryCondition orderBy(String field, String direction) {
//        this.addOrderBy(field, direction);
//        return this;
//    }
//
//    // ==================== 静态工厂方法 ====================
//
//    /**
//     * 创建新的查询条件
//     */
//    public static TenantQueryCondition create() {
//        return new TenantQueryCondition();
//    }
//
//    /**
//     * 创建关键词搜索条件
//     */
//    public static TenantQueryCondition keyword(String keyword) {
//        return create().code(keyword);
//    }
//}