package com.indigo.iam.sdk.vo.users;

import com.indigo.core.annotation.VoMapping;
import com.indigo.core.entity.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户权限关联查询VO（用于多表JOIN查询）
 * 用于查询用户拥有的权限编码
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
                @VoMapping.Field(source = "res.permissions", target = "permissions")
        }
)
public class UserPermissionVO extends BaseVO<String> {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 权限编码
     */
    private String permissions;
}

