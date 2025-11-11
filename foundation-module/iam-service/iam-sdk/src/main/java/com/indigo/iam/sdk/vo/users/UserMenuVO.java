package com.indigo.iam.sdk.vo.users;

import com.indigo.core.annotation.VoMapping;
import com.indigo.core.entity.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户菜单关联查询VO（用于多表JOIN查询）
 * 用于查询用户拥有的菜单信息
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

        // 关联表：角色菜单关联表 + 菜单表
        joins = {
                @VoMapping.Join(
                        table = "iam_role_menu",
                        alias = "rm",
                        type = VoMapping.JoinType.LEFT,
                        on = "ur.role_id = rm.role_id"
                ),
                @VoMapping.Join(
                        table = "iam_menu",
                        alias = "m",
                        type = VoMapping.JoinType.LEFT,
                        on = "rm.menu_id = m.id"
                )
        },
        // 字段映射
        fields = {
                // 用户角色关联字段
                @VoMapping.Field(source = "ur.user_id", target = "userId"),

                // 菜单字段
                @VoMapping.Field(source = "m.id", target = "menuId"),
                @VoMapping.Field(source = "m.system_id", target = "systemId"),
                @VoMapping.Field(source = "m.parent_id", target = "parentId"),
                @VoMapping.Field(source = "m.code", target = "code"),
                @VoMapping.Field(source = "m.name", target = "name"),
                @VoMapping.Field(source = "m.router", target = "router"),
                @VoMapping.Field(source = "m.component", target = "component"),
                @VoMapping.Field(source = "m.icon", target = "icon"),
                @VoMapping.Field(source = "m.status", target = "status"),
                @VoMapping.Field(source = "m.visible", target = "visible"),
                @VoMapping.Field(source = "m.create_time", target = "createTime"),
                @VoMapping.Field(source = "m.modify_time", target = "modifyTime")
        }
)
public class UserMenuVO extends BaseVO<String> {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 菜单ID
     */
    private String menuId;

    /**
     * 系统ID
     */
    private String systemId;

    /**
     * 父菜单ID
     */
    private String parentId;

    /**
     * 菜单编码
     */
    private String code;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 路由地址
     */
    private String router;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 是否可见
     */
    private Boolean visible;
}

