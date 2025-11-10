package com.indigo.iam.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.indigo.databases.entity.CreatedEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 角色菜单关联实体类
 * 
 * @author 史偕成
 * @date 2025/11/10
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
@TableName(value = "iam_role_menu")
public class RoleMenu extends CreatedEntity<String> {

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 菜单id
     */
    private String menuId;
}

