package com.indigo.iam.api.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 史偕成
 * @date 2025/07/28 07:04
 **/
@Data
public class TenantDTO implements Serializable {

    /**
     * 租户Id
     */
    private String id;

    /**
     * 租户名称
     */
    private String description;

    /**
     * 租户编码
     */
    private String code;
}
