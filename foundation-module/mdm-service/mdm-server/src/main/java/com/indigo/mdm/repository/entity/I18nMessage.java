package com.indigo.mdm.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.indigo.databases.entity.AuditEntity;
import com.indigo.databases.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 国际化消息实体
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
@TableName("sys_i18n_message")
public class I18nMessage extends AuditEntity<String> {
    /**
     * 错误码，关联sys_error_code.error_code
     */
    private String errorCode;

    /**
     * 语言代码，关联sys_language.language_code
     */
    private String languageCode;

    /**
     * 消息键：error.iam002
     */
    private String messageKey;

    /**
     * 消息模板：用户不存在：{0}
     */
    private String messageTemplate;

    /**
     * 消息参数配置：{"0":"userId","1":"userName"}
     */
    private String messageParams;

    /**
     * 是否启用：1-启用，0-禁用
     */
    private Boolean enabled;

    /**
     * 版本号
     */
    @TableField("version")
    private Integer version;
}
