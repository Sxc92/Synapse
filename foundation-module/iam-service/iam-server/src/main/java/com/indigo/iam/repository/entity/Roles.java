package com.indigo.iam.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.indigo.databases.entity.AuditEntity;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @date 2025/11/06 16:34
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
@TableName(value = "iam_role")
public class Roles extends AuditEntity<String> {

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态
     */
    private Boolean status;
}
