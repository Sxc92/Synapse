package com.indigo.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indigo.core.entity.TreeNode;
import com.indigo.core.exception.TreeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author 史偕成
 * @date 2025/04/20 16:59
 **/
@Slf4j
public class TreeUtil<T> {

    /**
     * -- GETTER --
     * 🌲 获取根节点
     */
    @Getter
    private final TreeNode<T> root;
    // 高效查找 & 线程安全
    private final Map<T, TreeNode<T>> nodeMap = new ConcurrentHashMap<>();

    public TreeUtil(T rootValue) {
        if (rootValue == null) {
            throw new TreeException("根节点不能为空");
        }
        this.root = new TreeNode<>(rootValue);
        nodeMap.put(rootValue, root);
    }

    /**
     * 🌳 获取所有节点列表
     */
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        depthFirstToList(root, result);
        return result;
    }

    private void depthFirstToList(TreeNode<T> node, List<T> list) {
        if (node == null) return;
        list.add(node.value());
        for (TreeNode<T> child : node.getChildren()) {
            depthFirstToList(child, list);
        }
    }


    /**
     * 🚀 通过 Map<id, parentId> 构建树（防止循环引用）
     */
    public static <T> TreeUtil<T> buildTree(T rootValue, List<T> nodes, Function<T, T> parentExtractor) {
        if (rootValue == null) {
            throw new TreeException("根节点不能为空");
        }
        if (nodes == null) {
            throw new TreeException("节点列表不能为空");
        }
        if (parentExtractor == null) {
            throw new TreeException("父节点映射函数不能为空");
        }

        // 构建所有节点
        Map<T, TreeNode<T>> nodeMap = new HashMap<>();
        for (T node : nodes) {
            nodeMap.put(node, new TreeNode<>(node));
        }

        TreeUtil<T> treeUtil = new TreeUtil<>(rootValue);
        Set<T> visited = new HashSet<>(); // 用于循环检测

        for (T node : nodes) {
            T parent = parentExtractor.apply(node);
            if (parent != null) {
                if (visited.contains(node)) {
                    throw new TreeException("检测到循环引用: " + node);
                }
                visited.add(node);
                nodeMap.getOrDefault(parent, treeUtil.root).addChild(nodeMap.get(node));
            } else {
                treeUtil.root.addChild(nodeMap.get(node));
            }
        }
        return treeUtil;
    }


    /**
     * ❌ 删除节点及其所有子节点
     */
    public synchronized boolean removeNode(T value) {
        if (value == null || value.equals(root.value())) {
            throw new TreeException("不能删除根节点");
        }

        TreeNode<T> toRemove = nodeMap.get(value);
        if (toRemove == null) return false;

        for (TreeNode<T> node : nodeMap.values()) {
            if (node.getChildren().contains(toRemove)) {
                node.removeChild(toRemove);
                nodeMap.remove(value);
                return true;
            }
        }
        return false;
    }

    /**
     * 🔍 查找从根到目标节点的路径
     */
    public synchronized List<T> findPath(T target) {
        if (target == null) {
            throw new TreeException("目标节点不能为空");
        }
        
        List<T> path = new ArrayList<>();
        if (findPathDFS(root, target, path)) {
            return path;
        }
        return Collections.emptyList();
    }

    private boolean findPathDFS(TreeNode<T> node, T target, List<T> path) {
        if (node == null) return false;
        path.add(node.value());
        if (node.value().equals(target)) return true;
        for (TreeNode<T> child : node.getChildren()) {
            if (findPathDFS(child, target, path)) {
                return true;
            }
        }
        path.remove(path.size() - 1);
        return false;
    }

    /** 📊 统计某个节点的子树大小 */
    public synchronized int countNodes(T value) {
        if (value == null) {
            throw new TreeException("节点值不能为空");
        }
        
        TreeNode<T> node = nodeMap.get(value);
        return node == null ? 0 : countNodesDFS(node);
    }

    private int countNodesDFS(TreeNode<T> node) {
        int count = 1;
        for (TreeNode<T> child : node.getChildren()) {
            count += countNodesDFS(child);
        }
        return count;
    }

    /** 📝 序列化为 JSON */
    public String toJson() throws JsonProcessingException {
        try {
            return new ObjectMapper().writeValueAsString(root);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize tree to JSON", e);
            throw new TreeException("Failed to serialize tree to JSON", e);
        }
    }

    /** 🔄 反序列化 JSON 为 Tree */
    public static <T> TreeUtil<T> fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        if (json == null || json.isEmpty()) {
            throw new TreeException("JSON 字符串不能为空");
        }
        if (clazz == null) {
            throw new TreeException("节点类型不能为空");
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            TreeNode<T> rootNode = mapper.readValue(json, mapper.getTypeFactory().constructParametricType(TreeNode.class, clazz));
            return new TreeUtil<>(rootNode.value());
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to tree", e);
            throw new TreeException("Failed to deserialize JSON to tree", e);
        }
    }
}
