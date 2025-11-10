package com.indigo.iam.repository.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.indigo.databases.entity.AuditEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @date 2025/11/08 17:10
 **/
@TableName("iam_menu")
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
public class Menu extends AuditEntity<String> {

    private String systemId;

    private String parentId;

    private String code;

    private String name;

    private String router;

    private String component;

    private String icon;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NOT_NULL)
    private Boolean status;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NOT_NULL)
    private Boolean visible;
}
