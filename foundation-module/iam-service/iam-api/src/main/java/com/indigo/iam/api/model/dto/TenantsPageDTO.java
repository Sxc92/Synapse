package com.indigo.iam.api.model.dto;

import com.indigo.databases.annotation.QueryCondition;
import com.indigo.databases.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/07/28 16:44
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TenantsPageDTO extends PageDTO {

    @QueryCondition(type = QueryCondition.QueryType.IN)
    private List<Integer> status;

    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String code;

    @QueryCondition(type = QueryCondition.QueryType.LIKE)
    private String description;
}
