package com.indigo.iam.api.model.dto;

import com.indigo.databases.annotation.QueryCondition;
import com.indigo.databases.dto.PageDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 租户查询DTO
 * 支持自动查询条件构建
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class TenantQueryDTO extends PageDTO {

    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String code;

    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String description;

    @QueryCondition(type = QueryCondition.QueryType.EQ)
    private Integer status;

    @QueryCondition(type = QueryCondition.QueryType.BETWEEN, field = "create_time")
    private LocalDateTime[] createTime;

    @QueryCondition(type = QueryCondition.QueryType.BETWEEN, field = "expire_time")
    private LocalDateTime[] expireTime;

    // ==================== 静态工厂方法（使用Lombok Builder） ====================
    
    /**
     * 创建状态查询条件
     */
    public static TenantQueryDTO byStatus(Integer status) {
        return TenantQueryDTO.builder()
            .status(status)
            .build();
    }

    /**
     * 创建关键词搜索条件
     */
    public static TenantQueryDTO byKeyword(String keyword) {
        return TenantQueryDTO.builder()
            .code(keyword)
            .build();
    }

    /**
     * 创建代码查询条件
     */
    public static TenantQueryDTO byCode(String code) {
        return TenantQueryDTO.builder()
            .code(code)
            .build();
    }

    /**
     * 创建时间范围查询条件
     */
    public static TenantQueryDTO byCreateTimeRange(LocalDateTime start, LocalDateTime end) {
        return TenantQueryDTO.builder()
            .createTime(new LocalDateTime[]{start, end})
            .build();
    }

    /**
     * 创建过期时间范围查询条件
     */
    public static TenantQueryDTO byExpireTimeRange(LocalDateTime start, LocalDateTime end) {
        return TenantQueryDTO.builder()
            .expireTime(new LocalDateTime[]{start, end})
            .build();
    }

    /**
     * 创建描述模糊查询条件
     */
    public static TenantQueryDTO byDescription(String description) {
        return TenantQueryDTO.builder()
            .description(description)
            .build();
    }
} 