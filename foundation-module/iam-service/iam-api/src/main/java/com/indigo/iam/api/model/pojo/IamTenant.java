package com.indigo.iam.api.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.indigo.databases.annotation.QueryCondition;
import com.indigo.databases.entity.AuditEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author 史偕成
 * @date 2025/07/28 06:09
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("tenants")
public class IamTenant extends AuditEntity<String> {
    /**
     * 租户编码
     */
    private String code;

    private String description;

    /**
     * 状态 0:停用、1:启用
     */
    private Integer status;

    private LocalDateTime expireTime;

}
