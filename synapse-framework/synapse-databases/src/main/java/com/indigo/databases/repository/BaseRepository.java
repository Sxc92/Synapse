package com.indigo.databases.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.indigo.databases.dto.PageDTO;
import com.indigo.databases.dto.PageResult;
import com.indigo.databases.utils.LambdaQueryBuilder;
import com.indigo.databases.utils.QueryConditionBuilder;

import java.util.List;
import java.util.Map;

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
     * 分页查询 - 仅使用Map条件
     */
    default IPage<T> pageWithCondition(Page<T> page, Map<String, Object> conditions) {
        PageResult<T> result = LambdaQueryBuilder.pageWithCondition(getMapper(), page, conditions);
        
        // 转换为 IPage 格式
        Page<T> resultPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        resultPage.setRecords(result.getRecords());
        return resultPage;
    }
    
    /**
     * 列表查询 - 支持自动查询条件构建
     */
    default List<T> listWithCondition(T queryEntity) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryEntity);
        return list(wrapper);
    }
    
    /**
     * 列表查询 - 支持Map额外条件
     */
    default List<T> listWithCondition(T queryEntity, Map<String, Object> extraConditions) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryEntity, extraConditions);
        return list(wrapper);
    }
    
    /**
     * 列表查询 - 仅使用Map条件
     */
    default List<T> listWithCondition(Map<String, Object> conditions) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(conditions);
        return list(wrapper);
    }
    
    /**
     * 单个查询 - 支持自动查询条件构建
     */
    default T getOneWithCondition(T queryEntity) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryEntity);
        return getOne(wrapper);
    }
    
    /**
     * 单个查询 - 支持Map额外条件
     */
    default T getOneWithCondition(T queryEntity, Map<String, Object> extraConditions) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryEntity, extraConditions);
        return getOne(wrapper);
    }
    
    /**
     * 单个查询 - 仅使用Map条件
     */
    default T getOneWithCondition(Map<String, Object> conditions) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(conditions);
        return getOne(wrapper);
    }
    
    /**
     * 统计查询 - 支持自动查询条件构建
     */
    default long countWithCondition(T queryEntity) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryEntity);
        return count(wrapper);
    }
    
    /**
     * 统计查询 - 支持Map额外条件
     */
    default long countWithCondition(T queryEntity, Map<String, Object> extraConditions) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryEntity, extraConditions);
        return count(wrapper);
    }
    
    /**
     * 统计查询 - 仅使用Map条件
     */
    default long countWithCondition(Map<String, Object> conditions) {
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(conditions);
        return count(wrapper);
    }
} 