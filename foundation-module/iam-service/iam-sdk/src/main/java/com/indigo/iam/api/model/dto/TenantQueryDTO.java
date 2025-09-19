package com.indigo.iam.api.model.dto;

import com.indigo.databases.annotation.QueryCondition;
import com.indigo.databases.dto.PageDTO;
import lombok.*;

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
@AllArgsConstructor
@NoArgsConstructor
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
}