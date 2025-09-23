package com.indigo.mdm.sdk.dto;

import com.indigo.core.entity.dto.QueryDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 语言支持DTO
 *
 * @author 史偕成
 * @date 2025/01/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LanguageDTO extends QueryDTO {

    /**
     * 语言代码：zh_CN,en,cs等
     */
    @NotBlank(message = "语言代码不能为空")
    private String languageCode;

    /**
     * 语言名称：中文,English,Čeština等
     */
    @NotBlank(message = "语言名称不能为空")
    private String languageName;

    /**
     * 本地名称：中文,English,Čeština等
     */
    @NotBlank(message = "本地名称不能为空")
    private String nativeName;

    /**
     * 国家代码：CN,US,CZ等
     */
    private String countryCode;

    /**
     * 完整locale：zh_CN,en_US,cs_CZ等
     */
    @NotBlank(message = "Locale不能为空")
    private String locale;

    /**
     * 是否默认语言：1-是，0-否
     */
    @NotNull(message = "默认语言状态不能为空")
    private Boolean isDefault;

    /**
     * 是否启用：1-启用，0-禁用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}
