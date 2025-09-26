package com.indigo.mdm.sdk.dto;

import com.indigo.core.entity.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 史偕成
 * @date 2025/09/26 11:08
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CountryDTO extends BaseDTO<String> {

    private String name;

    private String code;

}
