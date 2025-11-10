package com.indigo.iam.sdk.vo.users;

import com.indigo.core.annotation.VoMapping;
import com.indigo.core.entity.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户与角色关联查询VO（用于多表JOIN查询）
 * 注意：这个VO用于接收JOIN查询的扁平化结果，然后在Service层组装成UserDetailVO
 *
 * @author 史偕成
 */
@Data
@EqualsAndHashCode(callSuper = true)
@VoMapping(
        // 主表：用户表
        table = "iam_user",
        alias = "u",

        // 关联表：用户角色中间表 + 角色表
        joins = {
                // 关联用户角色中间表
                @VoMapping.Join(
                        table = "iam_user_role",
                        alias = "ur",
                        type = VoMapping.JoinType.LEFT,
                        on = "u.id = ur.user_id"
                ),
                // 关联角色表（注意：Roles.java的@TableName写的是iam_users，这里假设实际表名是iam_roles）
                @VoMapping.Join(
                        table = "iam_role",
                        alias = "r",
                        type = VoMapping.JoinType.LEFT,
                        on = "ur.role_id = r.id"
                )
        },
        // 字段映射
        fields = {
                // 用户字段
                @VoMapping.Field(source = "u.id", target = "id"),
                @VoMapping.Field(source = "u.account", target = "account"),
                @VoMapping.Field(source = "u.type", target = "userType"),
                @VoMapping.Field(source = "u.enabled", target = "enabled"),
                @VoMapping.Field(source = "u.locked", target = "locked"),
                @VoMapping.Field(source = "u.expired", target = "expired"),

                // 角色字段（一对多，这里会返回多条记录）
                @VoMapping.Field(source = "r.id", target = "roleId"),
                @VoMapping.Field(source = "r.code", target = "roleCode"),
                @VoMapping.Field(source = "r.description", target = "roleDesc"),
                @VoMapping.Field(source = "r.status", target = "roleStatus")
        }
)
public class UserWithRoleVO extends BaseVO<String> {
    // 用户字段
    private String account;
    private String userType;
    private Boolean enabled;
    private Boolean locked;
    private Boolean expired;

    // 角色字段（注意：由于是一对多，查询会返回多条记录，需要在Service层分组）
    private String roleId;
    private String roleCode;
    private String roleDesc;
    private Boolean roleStatus;
}

