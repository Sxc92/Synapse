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
 * @date 2025/11/10
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class ResourceDTO extends PageDTO<String> {

    @QueryCondition(field = "code", type = QueryCondition.QueryType.LIKE)
    private String code;

    @QueryCondition(field = "name", type = QueryCondition.QueryType.LIKE)
    private String name;

    @QueryCondition(field = "menu_id", type = QueryCondition.QueryType.EQ)
    private String menuId;

    @QueryCondition(field = "type", type = QueryCondition.QueryType.EQ)
    private String type;

    @QueryCondition(field = "permissions", type = QueryCondition.QueryType.LIKE)
    private String permissions;

    private String systemId;
}

