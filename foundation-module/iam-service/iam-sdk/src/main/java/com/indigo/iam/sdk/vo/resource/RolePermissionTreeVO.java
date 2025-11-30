package com.indigo.iam.sdk.vo.resource;

import com.indigo.iam.sdk.enums.PermissionNodeType;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色权限树形结构视图对象
 * 用于展示角色关联的系统、菜单和资源树形结构
 * 
 * @author 史偕成
 * @date 2025/11/27
 */
@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RolePermissionTreeVO {

    /**
     * 节点类型：SYSTEM（系统）、MENU（菜单）、RESOURCE（资源）
     */
    private PermissionNodeType type;

    /**
     * 节点ID
     */
    private String id;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 资源类型（仅 type=RESOURCE 时使用）：API、BUTTON 等
     */
    private String resourceType;

    /**
     * 资源编码（仅 type=resource 时使用）
     */
    private String code;

    /**
     * 是否选中（根据角色关联关系判断）
     */
    private Boolean selected;

    /**
     * 子节点列表
     */
    private List<RolePermissionTreeVO> children;

    /**
     * 获取子节点列表（如果为 null 则初始化为空列表）
     * 
     * @return 子节点列表
     */
    public List<RolePermissionTreeVO> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return this.children;
    }

    /**
     * 添加子节点
     * 
     * @param child 子节点
     */
    public void addChild(RolePermissionTreeVO child) {
        getChildren().add(child);
    }
}

