package com.indigo.iam.sdk.vo.resource;

import com.indigo.core.entity.vo.BaseVO;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @date 2025/11/08 17:12
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MenuVO extends BaseVO<String> {

    private String systemId;

    private String parentId;

    private String code;

    private String name;

    private String router;

    private String component;

    private String icon;

    private Boolean status;

    private Boolean visible;
}
