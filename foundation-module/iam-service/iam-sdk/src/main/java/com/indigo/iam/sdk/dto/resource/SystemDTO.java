package com.indigo.iam.sdk.dto.resource;

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
 * @date 2025/11/08 16:17
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class SystemDTO extends PageDTO<String> {

    @QueryCondition(field = "code", type = QueryCondition.QueryType.LIKE)
    private String code;

    @QueryCondition(field = "name", type = QueryCondition.QueryType.LIKE)
    private String name;

    @QueryCondition(field = "status", type = QueryCondition.QueryType.EQ)
    private Boolean status;
}
