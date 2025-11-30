package com.indigo.iam.repository.entity;

/**
 * 角色系统关联实体类
 *
 * <p><b>注意：</b>此功能已暂时注释，待业务完整后扩展
 * 暂时取消 iam_role_system 表，通过菜单推导系统关系
 * 系统关系可以通过 role_menu -> menu -> system_id 推导
 *
 * @author 史偕成
 * @date 2025/11/20
 */
// TODO: 待业务完整后恢复角色系统关联实体

import com.baomidou.mybatisplus.annotation.TableName;
import com.indigo.databases.entity.CreatedEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
@TableName(value = "iam_role_system")
public class RoleSystem extends CreatedEntity<String> {

    private String roleId;

    private String systemId;
}

