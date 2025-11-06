package com.indigo.iam.sdk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户类型枚举
 *
 * @author 史偕成
 * @date 2025/11/06 15:35
 **/
@Getter
@AllArgsConstructor
public enum UserTypeEnum {

    /**
     * 内部用户
     */
    INNER,

    /**
     * 供应商用户
     */
    SUPPLIER,

    /**
     * 客户用户
     */
    CUSTOMER,


}
