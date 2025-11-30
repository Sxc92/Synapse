package com.indigo.iam.sdk.vo.resource;

import com.indigo.core.annotation.VoMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源详情VO（用于多表JOIN查询）
 * 关联查询菜单表和系统表，获取菜单的 name 和 code，以及系统的 name 和 code
 * 注意：资源必须关联菜单，系统关系通过 menu -> system_id 推导
 *
 * @author 史偕成
 * @date 2025/11/24 11:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
@VoMapping(
        // 主表：资源表
        table = "iam_resources",
        alias = "res",
        
        // 关联表：菜单表 + 系统表（通过菜单关联系统）
        joins = {
                @VoMapping.Join(
                        table = "iam_menu",
                        alias = "menu",
                        type = VoMapping.JoinType.LEFT,
                        on = "res.menu_id = menu.id"
                ),
                @VoMapping.Join(
                        table = "iam_system",
                        alias = "sys",
                        type = VoMapping.JoinType.LEFT,
                        on = "menu.system_id = sys.id"
                )
        },
        // 字段映射
        fields = {
                // 资源字段（继承自 ResourceVO）
                @VoMapping.Field(source = "res.id", target = "id"),
                @VoMapping.Field(source = "res.menu_id", target = "menuId"),
                @VoMapping.Field(source = "res.code", target = "code"),
                @VoMapping.Field(source = "res.name", target = "name"),
                @VoMapping.Field(source = "res.type", target = "type"),
                @VoMapping.Field(source = "res.description", target = "description"),
                @VoMapping.Field(source = "res.permissions", target = "permissions"),
                
                // 菜单字段
                @VoMapping.Field(source = "menu.code", target = "menuCode"),
                @VoMapping.Field(source = "menu.name", target = "menuName"),
                
                // 系统字段（通过菜单推导）
                @VoMapping.Field(source = "sys.code", target = "systemCode"),
                @VoMapping.Field(source = "sys.name", target = "systemName")
        }
)
public class ResourceDetailVO extends ResourceVO {
    
    /**
     * 系统编码
     */
    private String systemCode;
    
    /**
     * 系统名称
     */
    private String systemName;
    
    /**
     * 菜单编码
     */
    private String menuCode;
    
    /**
     * 菜单名称
     */
    private String menuName;
}
