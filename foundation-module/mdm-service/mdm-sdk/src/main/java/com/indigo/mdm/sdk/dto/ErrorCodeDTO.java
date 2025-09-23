package com.indigo.mdm.sdk.dto;


import com.indigo.core.entity.dto.QueryDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 错误码DTO
 *
 * @author 史偕成
 * @date 2025/01/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ErrorCodeDTO extends QueryDTO {

    /**
     * 错误码，如IAM002
     */
    @NotBlank(message = "错误码不能为空")
    private String errorCode;

    /**
     * 错误名称
     */
    @NotBlank(message = "错误名称不能为空")
    private String errorName;

    /**
     * 错误分类：SYSTEM,BUSINESS,SECURITY等
     */
    @NotBlank(message = "错误分类不能为空")
    private String errorCategory;

    /**
     * 模块代码：IAM,MDM,GATEWAY等
     */
    @NotBlank(message = "模块代码不能为空")
    private String moduleCode;

    /**
     * 严重级别：ERROR,WARN,INFO
     */
    @NotBlank(message = "严重级别不能为空")
    private String severityLevel;

    /**
     * 错误描述
     */
    private String description;

    /**
     * 是否启用：1-启用，0-禁用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;
}
