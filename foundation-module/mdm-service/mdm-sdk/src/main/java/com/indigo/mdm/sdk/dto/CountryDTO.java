package com.indigo.mdm.sdk.dto;

import com.indigo.core.entity.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author 史偕成
 * @date 2025/09/26 11:08
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CountryDTO extends BaseDTO<String> {

    @NotBlank(message = "国家名称不能为空")
    @Size(max = 16, message = "国家名称长度不能超过16个字符")
    private String name;

    @NotBlank(message = "国家编码不能为空")
    @Size(max = 255, message = "国家编码长度不能超过255个字符")
    private String code;

}
