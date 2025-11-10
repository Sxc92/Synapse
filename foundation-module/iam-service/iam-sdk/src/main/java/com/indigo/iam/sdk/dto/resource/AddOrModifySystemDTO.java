package com.indigo.iam.sdk.dto.resource;

import com.indigo.core.entity.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @date 2025/11/08 15:57
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AddOrModifySystemDTO extends BaseDTO<String> {

    private String code;

    private String name;

    private Boolean status;

    private Integer sorted;
}
