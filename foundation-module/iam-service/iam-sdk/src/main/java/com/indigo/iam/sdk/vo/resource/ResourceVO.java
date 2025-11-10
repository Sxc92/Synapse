package com.indigo.iam.sdk.vo.resource;

import com.indigo.core.entity.vo.BaseVO;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author 史偕成
 * @date 2025/11/10
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ResourceVO extends BaseVO<String> {

    /**
     * 系统Id
     */
    private String systemId;

    /**
     * 菜单Id（只有FUNCTION类型存在）
     */
    private String menuId;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型：API、DATA、FUNCTION（前端按钮）
     */
    private String type;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 权限编码
     */
    private String permissions;
}

