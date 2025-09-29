package com.indigo.mdm.sdk.dto.enums;

import com.indigo.core.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MdmError implements ErrorCode {
    /**
     * 国家编码重复
     */
    COUNTRY_CODE_DUPLICATION("MDM001"),

    /**
     * 国家不存在
     */
    COUNTRY_NOT_FOUND("MDM002"),
    ;

    private final String code;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessageKey() {
        return ErrorCode.super.getMessageKey();
    }
}
