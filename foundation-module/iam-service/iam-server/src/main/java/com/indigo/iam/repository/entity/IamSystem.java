package com.indigo.iam.repository.entity;

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
 * @date 2025/11/08 15:36
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
@TableName(value = "iam_system")
public class IamSystem extends AuditEntity<String> {

    /**
     * 系统编码
     */
    private String code;

    /**
     * 系统名称
     */
    private String name;

    /**
     * 系统状态
     */
    private Boolean status;

    /**
     * 排序
     */
    private Integer sorted;
}
