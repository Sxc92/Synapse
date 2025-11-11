package com.indigo.iam.sdk.vo.users;

import com.indigo.core.annotation.VoMapping;
import com.indigo.core.entity.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联查询VO（用于多表JOIN查询）
 * 用于查询用户拥有的角色信息
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

        // 关联表：角色表
        joins = {
                @VoMapping.Join(
                        table = "iam_role",
                        alias = "r",
                        type = VoMapping.JoinType.LEFT,
                        on = "ur.role_id = r.id"
                )
        },
        // 字段映射
        fields = {
                // 用户角色关联字段
                @VoMapping.Field(source = "ur.user_id", target = "userId"),
                @VoMapping.Field(source = "ur.role_id", target = "roleId"),

                // 角色字段
                @VoMapping.Field(source = "r.code", target = "roleCode"),
                @VoMapping.Field(source = "r.description", target = "roleDesc"),
                @VoMapping.Field(source = "r.status", target = "roleStatus")
        }
)
public class UserRoleVO extends BaseVO<String> {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String roleDesc;

    /**
     * 角色状态
     */
    private Boolean roleStatus;
}

