package com.indigo.iam.sdk.vo.resource;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树结构视图对象
 * 
 * @author 史偕成
 * @date 2025/01/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MenuTreeVO extends MenuVO {

    /**
     * 子菜单列表
     */
    private List<MenuTreeVO> children;

    /**
     * 获取子菜单列表（如果为 null 则初始化为空列表）
     * 
     * @return 子菜单列表
     */
    public List<MenuTreeVO> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return this.children;
    }

    /**
     * 添加子菜单
     * 
     * @param child 子菜单
     */
    public void addChild(MenuTreeVO child) {
        getChildren().add(child);
    }
}

