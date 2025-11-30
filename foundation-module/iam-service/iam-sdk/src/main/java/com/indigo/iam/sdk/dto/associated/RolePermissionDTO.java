package com.indigo.iam.sdk.dto.associated;

import com.indigo.core.validation.NotEmptyWithErrorCode;
import com.indigo.iam.sdk.enums.IamError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 角色权限分配DTO
 * 
 * @author 史偕成
 * @date 2025/11/29 10:55
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class RolePermissionDTO implements Serializable {

    /**
     * 系统Id列表
     */
    @NotEmptyWithErrorCode(
        errorCodeEnum = IamError.class,
        errorCodeName = "SYSTEM_IDS_EMPTY"
    )
    private List<String> systemIds;

    /**
     * 菜单Id列表
     */
    @NotEmptyWithErrorCode(
        errorCodeEnum = IamError.class,
        errorCodeName = "MENU_IDS_EMPTY"
    )
    private List<String> menuIds;

    /**
     * 资源Id列表
     */
    @NotEmptyWithErrorCode(
        errorCodeEnum = IamError.class,
        errorCodeName = "RESOURCE_IDS_EMPTY"
    )
    private List<String> resourceIds;
}
