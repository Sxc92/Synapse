package com.indigo.iam.sdk.dto.opera;

import com.indigo.core.entity.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @date 2025/11/07 15:37
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AddOrModifyMenuDTO extends BaseDTO<String> {

    private String systemId;

    private String parentId;

    private String code;

    private String name;

    private String icon;

    private String router;

    private String component;

    private Boolean visible;

    private Boolean status;
}
