package com.indigo.mdm.sdk.dto.query;

import com.indigo.core.annotation.QueryCondition;
import com.indigo.core.entity.dto.QueryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @date 2025/09/26 11:11
 **/
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CountryQueryDTO extends QueryDTO<String> {

    @QueryCondition(field = "name", type = QueryCondition.QueryType.LIKE)
    private String name;

    @QueryCondition(field = "code", type = QueryCondition.QueryType.LIKE)
    private String code;
}
