package com.indigo.mdm.sdk.vo;

import com.indigo.core.entity.vo.BaseVO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 史偕成
 * @date 2025/09/29 22:26
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CountryVO extends BaseVO<String> {

    private String code;

    private String name;

    private Boolean enabled;
}
