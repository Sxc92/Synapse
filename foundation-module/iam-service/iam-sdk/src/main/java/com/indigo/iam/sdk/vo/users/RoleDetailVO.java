package com.indigo.iam.sdk.vo.users;

import com.indigo.core.entity.vo.BaseVO;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/06 16:55
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RoleDetailVO extends BaseVO<String> {

    private String roleCode;

    private String roleName;

    private String roleDesc;

    private List<String> permissionCodes;

    private List<String> menus;
}
