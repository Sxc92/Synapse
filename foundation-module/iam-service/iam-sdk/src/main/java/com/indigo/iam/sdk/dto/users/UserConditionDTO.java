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
public class UserConditionDTO extends QueryDTO {

    @QueryCondition(field = "account", type = QueryCondition.QueryType.EQ)
    private String username;
}
