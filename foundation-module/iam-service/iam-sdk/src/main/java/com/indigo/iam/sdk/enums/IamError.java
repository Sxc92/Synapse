package com.indigo.iam.sdk.enums;

import com.indigo.core.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 史偕成
 * @date 2025/11/07 13:44
 **/
@Getter
@AllArgsConstructor
public enum IamError implements ErrorCode {

    USER_NOT_EXIST("IAM001"),

    USER_ACCOUNT_EXIST("IAM002"),

    ROLE_NOT_EXIST("IAM003"),

    ROLE_EXIST("IAM004"),

    SYSTEM_NOT_EXIST("IAM005"),

    SYSTEM_EXIST("IAM006"),

    MENU_NOT_EXIST("IAM007"),

    MENU_EXIST("IAM008"),

    RESOURCE_NOT_EXIST("IAM009"),

    RESOURCE_EXIST("IAM010"),
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
