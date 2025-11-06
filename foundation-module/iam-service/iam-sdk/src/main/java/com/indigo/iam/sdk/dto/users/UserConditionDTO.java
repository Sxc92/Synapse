package com.indigo.iam.sdk.dto.users;

import com.indigo.core.annotation.QueryCondition;
import com.indigo.core.entity.dto.QueryDTO;
import lombok.Data;

/**
 * @author 史偕成
 * @title
 * @description
 * @create 2025-09-22 17:16
 */
@Data
public class UserConditionDTO extends QueryDTO<String> {

    /**
     * 用户名查询条件
     * 注意：多表查询时需要使用表别名 u.account
     */
    @QueryCondition(field = "u.account", type = QueryCondition.QueryType.LIKE)
    private String username;
}
