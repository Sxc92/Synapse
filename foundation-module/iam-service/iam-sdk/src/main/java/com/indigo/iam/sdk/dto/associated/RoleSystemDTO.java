package com.indigo.iam.sdk.dto.associated;

/**
 * 角色系统关联 DTO
 * 
 * <p><b>注意：</b>此功能已暂时注释，待业务完整后扩展
 * 暂时取消 iam_role_system 表，通过菜单推导系统关系
 * 系统关系可以通过 role_menu -> menu -> system_id 推导
 * 
 * @author 史偕成
 * @date 2025/11/20
 */
// TODO: 待业务完整后恢复角色系统关联功能
/*
import com.indigo.core.entity.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class RoleSystemDTO extends BaseDTO<String> {

    private List<String> systemIds;
}
*/
