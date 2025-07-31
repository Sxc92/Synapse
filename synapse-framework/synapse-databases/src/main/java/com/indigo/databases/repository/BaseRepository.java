package com.indigo.databases.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.indigo.databases.dto.PageDTO;
import com.indigo.databases.dto.PageResult;
import com.indigo.databases.utils.LambdaQueryBuilder;
import com.indigo.databases.utils.QueryConditionBuilder;

import java.util.List;

/**
 * 基础Repository接口
 * 继承MyBatis-Plus的IService，提供所有基础CRUD功能
 * 同时支持注解SQL查询和自动查询条件构建
 *
 * @author 史偕成
 * @date 2024/12/19
 */
public interface BaseRepository<T, M extends BaseMapper<T>> extends IService<T> {
    
    /**
     * 获取Mapper实例
     */
    M getMapper();
    
    // ==================== 自动查询条件构建方法 ====================
    
    /**
     * 分页查询 - 支持DTO查询条件（推荐使用）
     * 约定：所有分页请求参数都要继承 PageDTO
     */
    default <D extends PageDTO> PageResult<T> pageWithCondition(D queryDTO) {
        // 直接使用 LambdaQueryBuilder 的静态方法，避免代理对象的问题
        return LambdaQueryBuilder.pageWithCondition(getMapper(), queryDTO);
    }
    
    /**
     * 列表查询 - 支持自动查询条件构建（实体类）
     */
    default List<T> listWithCondition(T queryEntity) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryEntity);
        return list(wrapper);
    }
    
    /**
     * 列表查询 - 支持DTO查询条件（推荐使用）
     */
    default <D> List<T> listWithCondition(D queryDTO) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryDTO);
        return list(wrapper);
    }
    
    /**
     * 单个查询 - 支持自动查询条件构建（实体类）
     */
    default T getOneWithCondition(T queryEntity) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryEntity);
        return getOne(wrapper);
    }
    
    /**
     * 单个查询 - 支持DTO查询条件（推荐使用）
     */
    default <D> T getOneWithCondition(D queryDTO) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryDTO);
        return getOne(wrapper);
    }
    
    /**
     * 统计查询 - 支持自动查询条件构建（实体类）
     */
    default long countWithCondition(T queryEntity) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryEntity);
        return count(wrapper);
    }
    
    /**
     * 统计查询 - 支持DTO查询条件（推荐使用）
     */
    default <D> long countWithCondition(D queryDTO) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryDTO);
        return count(wrapper);
    }
} 