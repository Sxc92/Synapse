package com.indigo.databases.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.databases.dto.PageDTO;
import com.indigo.databases.dto.PageResult;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Lambda查询构建器
 * 提供便捷的Lambda查询方法
 *
 * @author 史偕成
 * @date 2024/12/19
 */
public class LambdaQueryBuilder<T> {
    
    private final BaseMapper<T> mapper;
    private final LambdaQueryWrapper<T> queryWrapper;
    
    public LambdaQueryBuilder(BaseMapper<T> mapper) {
        this.mapper = mapper;
        this.queryWrapper = new LambdaQueryWrapper<>();
    }
    
    /**
     * 创建查询构建器
     */
    public static <T> LambdaQueryBuilder<T> of(BaseMapper<T> mapper) {
        return new LambdaQueryBuilder<>(mapper);
    }
    
    /**
     * 添加查询条件
     */
    public LambdaQueryBuilder<T> where(Consumer<LambdaQueryWrapper<T>> consumer) {
        consumer.accept(queryWrapper);
        return this;
    }
    
    /**
     * 等值查询
     */
    public LambdaQueryBuilder<T> eq(SFunction<T, ?> column, Object value) {
        if (value != null) {
            queryWrapper.eq(column, value);
        }
        return this;
    }
    
    /**
     * 不等值查询
     */
    public LambdaQueryBuilder<T> ne(SFunction<T, ?> column, Object value) {
        if (value != null) {
            queryWrapper.ne(column, value);
        }
        return this;
    }
    
    /**
     * 模糊查询
     */
    public LambdaQueryBuilder<T> like(SFunction<T, ?> column, String value) {
        if (value != null && !value.trim().isEmpty()) {
            queryWrapper.like(column, value);
        }
        return this;
    }
    
    /**
     * 左模糊查询
     */
    public LambdaQueryBuilder<T> likeLeft(SFunction<T, ?> column, String value) {
        if (value != null && !value.trim().isEmpty()) {
            queryWrapper.likeLeft(column, value);
        }
        return this;
    }
    
    /**
     * 右模糊查询
     */
    public LambdaQueryBuilder<T> likeRight(SFunction<T, ?> column, String value) {
        if (value != null && !value.trim().isEmpty()) {
            queryWrapper.likeRight(column, value);
        }
        return this;
    }
    
    /**
     * IN查询
     */
    public LambdaQueryBuilder<T> in(SFunction<T, ?> column, List<?> values) {
        if (values != null && !values.isEmpty()) {
            queryWrapper.in(column, values);
        }
        return this;
    }
    
    /**
     * 大于查询
     */
    public LambdaQueryBuilder<T> gt(SFunction<T, ?> column, Object value) {
        if (value != null) {
            queryWrapper.gt(column, value);
        }
        return this;
    }
    
    /**
     * 大于等于查询
     */
    public LambdaQueryBuilder<T> ge(SFunction<T, ?> column, Object value) {
        if (value != null) {
            queryWrapper.ge(column, value);
        }
        return this;
    }
    
    /**
     * 小于查询
     */
    public LambdaQueryBuilder<T> lt(SFunction<T, ?> column, Object value) {
        if (value != null) {
            queryWrapper.lt(column, value);
        }
        return this;
    }
    
    /**
     * 小于等于查询
     */
    public LambdaQueryBuilder<T> le(SFunction<T, ?> column, Object value) {
        if (value != null) {
            queryWrapper.le(column, value);
        }
        return this;
    }
    
    /**
     * 排序
     */
    public LambdaQueryBuilder<T> orderBy(SFunction<T, ?> column, boolean isAsc) {
        if (isAsc) {
            queryWrapper.orderByAsc(column);
        } else {
            queryWrapper.orderByDesc(column);
        }
        return this;
    }
    
    /**
     * 升序排序
     */
    public LambdaQueryBuilder<T> orderByAsc(SFunction<T, ?> column) {
        queryWrapper.orderByAsc(column);
        return this;
    }
    
    /**
     * 降序排序
     */
    public LambdaQueryBuilder<T> orderByDesc(SFunction<T, ?> column) {
        queryWrapper.orderByDesc(column);
        return this;
    }
    
    /**
     * 限制结果数量
     */
    public LambdaQueryBuilder<T> last(String lastSql) {
        queryWrapper.last(lastSql);
        return this;
    }
    
    /**
     * 查询列表
     */
    public List<T> list() {
        return mapper.selectList(queryWrapper);
    }
    
    /**
     * 查询单个
     */
    public T one() {
        return mapper.selectOne(queryWrapper);
    }
    
    /**
     * 查询数量
     */
    public Long count() {
        return mapper.selectCount(queryWrapper);
    }
    
    /**
     * 分页查询
     */
    public PageResult<T> page(PageDTO pageDTO) {
        Page<T> page = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());
        Page<T> result = mapper.selectPage(page, queryWrapper);
        
        return new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
    }
    
    /**
     * 分页查询 - 支持DTO查询条件
     */
    public static <T> PageResult<T> pageWithCondition(BaseMapper<T> mapper, PageDTO pageDTO) {
        // 构建查询条件
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(pageDTO);
        
        // 执行分页查询
        Page<T> page = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());
        Page<T> result = mapper.selectPage(page, wrapper);
        
        return new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
    }
    
    /**
     * 分页查询 - 支持实体查询条件
     */
    public static <T> PageResult<T> pageWithCondition(BaseMapper<T> mapper, Page<T> page, T queryEntity) {
        // 构建查询条件
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryEntity);
        
        // 执行分页查询
        Page<T> result = mapper.selectPage(page, wrapper);
        
        return new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
    }
    
    /**
     * 分页查询 - 支持实体和Map额外条件
     */
    public static <T> PageResult<T> pageWithCondition(BaseMapper<T> mapper, Page<T> page, T queryEntity, Map<String, Object> extraConditions) {
        // 构建查询条件
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(queryEntity, extraConditions);
        
        // 执行分页查询
        Page<T> result = mapper.selectPage(page, wrapper);
        
        return new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
    }
    
    /**
     * 分页查询 - 仅使用Map条件
     */
    public static <T> PageResult<T> pageWithCondition(BaseMapper<T> mapper, Page<T> page, Map<String, Object> conditions) {
        // 构建查询条件
        QueryWrapper<T> wrapper = QueryConditionBuilder.buildQueryWrapper(conditions);
        
        // 执行分页查询
        Page<T> result = mapper.selectPage(page, wrapper);
        
        return new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
    }
    
    /**
     * 获取QueryWrapper
     */
    public LambdaQueryWrapper<T> getQueryWrapper() {
        return queryWrapper;
    }
} 