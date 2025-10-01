package com.indigo.mdm.sdk.dto;

import com.indigo.core.entity.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 史偕成
 * @date 2025/09/30 08:54
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class LanguageDTO extends BaseDTO<String> {

    private String code;

    private String name;

    private Boolean sysDefault;

    private Boolean enabled;
}
