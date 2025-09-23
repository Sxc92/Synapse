package com.indigo.mdm.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.indigo.databases.entity.AuditEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 错误码管理实体
 *
 * @author 史偕成
 * @date 2025/01/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("sys_error_code")
public class ErrorCode extends AuditEntity<String> {

    /**
     * 错误码，如IAM002
     */
    private String errorCode;

    /**
     * 错误名称
     */
    private String errorName;

    /**
     * 错误分类：SYSTEM,BUSINESS,SECURITY等
     */
    private String errorCategory;

    /**
     * 模块代码：IAM,MDM,GATEWAY等
     */
    private String moduleCode;

    /**
     * 严重级别：ERROR,WARN,INFO
     */
    private String severityLevel;

    /**
     * 错误描述
     */
    private String description;

    /**
     * 是否启用：1-启用，0-禁用
     */
    private Boolean enabled;

    /**
     * 版本号
     */
    private Integer version;
}
