package com.indigo.iam.sdk.vo.resource;

import com.indigo.core.entity.vo.BaseVO;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author 史偕成
 * @date 2025/11/08 15:41
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SystemVO extends BaseVO<String> {

    /**
     * 系统编码
     */
    private String code;

    /**
     * 系统名称
     */
    private String name;

    /**
     * 系统状态
     */
    private Boolean status;

    /**
     * 排序
     */
    private Integer sorted;
}
