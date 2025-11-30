package com.indigo.iam.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.indigo.databases.entity.AuditEntity;
import com.indigo.iam.sdk.enums.ResourceTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 资源实体类
 *
 * @author 史偕成
 * @date 2025/11/10
 **/
@TableName("iam_resources")
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
public class IamResource extends AuditEntity<String> {

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
     * 类型：API、BUTTON（前端按钮）
     * {@link ResourceTypeEnums }
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

