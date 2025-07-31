package com.indigo.core.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author 史偕成
 * @date 2025/04/20 17:00
 **/
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class TreeNode<T> {

    private final T value;

    private final List<TreeNode<T>> children;

    public TreeNode(T value) {
        this.value = Objects.requireNonNull(value, "节点值不能为空");
        this.children = new ArrayList<>();
    }

    public List<TreeNode<T>> getChildren() {
        return Collections.unmodifiableList(children); // 保护不可变性
    }

    public void addChild(TreeNode<T> child) {
        Objects.requireNonNull(child, "子节点不能为空");
        this.children.add(child);
    }

    public boolean removeChild(TreeNode<T> child) {
        return this.children.remove(child);
    }

}
