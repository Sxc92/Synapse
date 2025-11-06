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
 * @author 史偕成
 * @date 2025/11/06 16:51
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
@TableName(value = "iam_user_role")
public class UsersRole extends CreatedEntity<String> {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 角色id
     */
    private String roleId;
}
