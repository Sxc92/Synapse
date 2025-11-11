package com.indigo.iam.sdk.vo.users;

import com.indigo.core.annotation.VoMapping;
import com.indigo.core.entity.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户资源关联查询VO（用于多表JOIN查询）
 * 用于查询用户拥有的资源信息
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@VoMapping(
        // 主表：用户角色关联表
        table = "iam_user_role",
        alias = "ur",

        // 关联表：角色资源关联表 + 资源表
        joins = {
                @VoMapping.Join(
                        table = "iam_role_resource",
                        alias = "rr",
                        type = VoMapping.JoinType.LEFT,
                        on = "ur.role_id = rr.role_id"
                ),
                @VoMapping.Join(
                        table = "iam_resources",
                        alias = "res",
                        type = VoMapping.JoinType.LEFT,
                        on = "rr.resource_id = res.id"
                )
        },
        // 字段映射
        fields = {
                // 用户角色关联字段
                @VoMapping.Field(source = "ur.user_id", target = "userId"),

                // 资源字段
                @VoMapping.Field(source = "res.id", target = "resourceId"),
                @VoMapping.Field(source = "res.system_id", target = "systemId"),
                @VoMapping.Field(source = "res.menu_id", target = "menuId"),
                @VoMapping.Field(source = "res.code", target = "code"),
                @VoMapping.Field(source = "res.name", target = "name"),
                @VoMapping.Field(source = "res.type", target = "type"),
                @VoMapping.Field(source = "res.description", target = "description"),
                @VoMapping.Field(source = "res.permissions", target = "permissions"),
                @VoMapping.Field(source = "res.create_time", target = "createTime"),
                @VoMapping.Field(source = "res.modify_time", target = "modifyTime")
        }
)
public class UserResourceVO extends BaseVO<String> {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 系统ID
     */
    private String systemId;

    /**
     * 菜单ID（只有FUNCTION类型存在）
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

