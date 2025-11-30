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
 * @date 2025/11/10
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AddOrModifyResourceDTO extends BaseDTO<String> {

    /**
     * 菜单Id（必填，资源必须关联菜单）
     * 系统关系通过 menu -> system_id 推导
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

