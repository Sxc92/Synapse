package com.indigo.iam.sdk.dto.users;

import com.indigo.core.annotation.QueryCondition;
import com.indigo.core.entity.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @date 2025/11/07 15:39
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class RoleDTO extends PageDTO<String> {

    @QueryCondition(field = "code", type = QueryCondition.QueryType.LIKE)
    private String code;

    @QueryCondition(field = "description", type = QueryCondition.QueryType.LIKE)
    private String description;

    @QueryCondition(field = "status", type = QueryCondition.QueryType.EQ)
    private Boolean status;
}
