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
 * 语言支持实体
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
@TableName("sys_language")
public class Language extends AuditEntity<String> {

    /**
     * 语言代码：zh_CN,en,cs等
     */
    private String languageCode;

    /**
     * 语言名称：中文,English,Čeština等
     */
    private String languageName;

    /**
     * 本地名称：中文,English,Čeština等
     */
    private String nativeName;

    /**
     * 国家代码：CN,US,CZ等
     */
    private String countryCode;

    /**
     * 完整locale：zh_CN,en_US,cs_CZ等
     */
    private String locale;

    /**
     * 是否默认语言：1-是，0-否
     */
    private Boolean isDefault;

    /**
     * 是否启用：1-启用，0-禁用
     */
    private Boolean enabled;
}
