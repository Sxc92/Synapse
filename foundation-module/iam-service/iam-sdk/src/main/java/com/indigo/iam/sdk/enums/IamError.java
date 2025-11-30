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

    USER_PASSWORD_ERROR("IAM014"),

    USER_LOCKED("IAM015"),

    USER_DISABLED("IAM016"),

    ROLE_NOT_EXIST("IAM003"),

    ROLE_EXIST("IAM004"),

    SYSTEM_NOT_EXIST("IAM005"),


    SYSTEM_EXIST("IAM006"),

    MENU_NOT_EXIST("IAM007"),

    MENU_EXIST("IAM008"),

    RESOURCE_NOT_EXIST("IAM009"),

    RESOURCE_EXIST("IAM010"),

    /**
     * 角色绑定菜单
     */
    ROLE_BIND_MENU("IAM011"),

    /**
     * 菜单绑定资源
     */
    RESOURCE_BIND_MENU("IAM012"),

    /**
     * 系统绑定菜单
     */
    SYSTEM_BIND_MENU("IAM013"),

    /**
     * 角色未拥有系统权限
     */
    ROLE_NOT_HAVE_SYSTEM("IAM017"),

    /**
     * 菜单不属于角色已拥有的系统
     */
    MENU_NOT_IN_ROLE_SYSTEM("IAM018"),

    /**
     * 资源不属于角色已拥有的系统
     */
    RESOURCE_NOT_IN_ROLE_SYSTEM("IAM019"),

    /**
     * 用户未分配资源权限
     */
    USER_NOT_HAVE_RESOURCE("IAM020"),

    /**
     * 用户未分配系统权限
     */
    USER_NOT_HAVE_SYSTEM("IAM021"),

    /**
     * 用户未分配菜单权限
     */
    USER_NOT_HAVE_MENU("IAM022"),

    /**
     * 系统ID列表为空
     */
    SYSTEM_IDS_EMPTY("IAM023"),

    /**
     * 菜单ID列表为空
     */
    MENU_IDS_EMPTY("IAM024"),

    /**
     * 资源ID列表为空
     */
    RESOURCE_IDS_EMPTY("IAM025"),

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
