package com.indigo.iam.sdk.dto.query;

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
 * @date 2025/11/08 17:14
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class MenuDTO extends PageDTO<String> {

    @QueryCondition(field = "code", type = QueryCondition.QueryType.LIKE)
    private String code;

    @QueryCondition(field = "name", type = QueryCondition.QueryType.LIKE)
    private String name;

    @QueryCondition(field = "parent_id", type = QueryCondition.QueryType.EQ)
    private String parentId;

    @QueryCondition(field = "system_id", type = QueryCondition.QueryType.EQ)
    private String systemId;
}
