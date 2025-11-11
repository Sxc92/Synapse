package com.indigo.iam.sdk.dto.query;

import com.indigo.core.annotation.QueryCondition;
import com.indigo.core.entity.dto.QueryDTO;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 用户资源查询DTO
 * 用于查询用户拥有的资源信息
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserResourceQueryDTO extends QueryDTO<String> {

    /**
     * 用户ID（必填）
     */
    @QueryCondition(field = "ur.user_id", type = QueryCondition.QueryType.EQ)
    private String userId;
}

