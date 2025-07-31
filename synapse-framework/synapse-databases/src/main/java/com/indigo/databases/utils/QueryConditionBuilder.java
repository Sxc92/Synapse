package com.indigo.databases.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.indigo.databases.annotation.QueryCondition;
import com.indigo.databases.dto.PageDTO;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * 查询条件构建器
 * 根据实体类字段的@QueryCondition注解自动构建LambdaQueryWrapper
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
public class QueryConditionBuilder {

        /**
     * 根据DTO对象构建查询条件（推荐使用）
     */
    public static <T> QueryWrapper<T> buildQueryWrapper(PageDTO queryDTO) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();

        if (queryDTO != null) {
            addEntityConditions(wrapper, queryDTO);
            addOrderByConditions(wrapper, queryDTO);
        }

        return wrapper;
    }

    /**
     * 根据实体对象构建查询条件
     */
    public static <T> QueryWrapper<T> buildQueryWrapper(T entity) {
        return buildQueryWrapper(entity, null);
    }

    /**
     * 根据实体对象和额外条件构建查询条件
     */
    public static <T> QueryWrapper<T> buildQueryWrapper(T entity, Map<String, Object> extraConditions) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        
        if (entity != null) {
            addEntityConditions(wrapper, entity);
        }
        
        if (extraConditions != null && !extraConditions.isEmpty()) {
            addExtraConditions(wrapper, extraConditions);
        }
        
        return wrapper;
    }

    /**
     * 仅根据Map条件构建查询条件
     */
    public static <T> QueryWrapper<T> buildQueryWrapper(Map<String, Object> conditions) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        
        if (conditions != null && !conditions.isEmpty()) {
            addExtraConditions(wrapper, conditions);
        }
        
        return wrapper;
    }

    /**
     * 添加实体类字段的查询条件
     */
    private static <T> void addEntityConditions(QueryWrapper<T> wrapper, Object entity) {
        try {
            Class<?> entityClass = entity.getClass();
            Field[] fields = entityClass.getDeclaredFields();
            
            for (Field field : fields) {
                QueryCondition annotation = field.getAnnotation(QueryCondition.class);
                if (annotation != null) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    
                    if (shouldIncludeValue(value, annotation)) {
                        addCondition(wrapper, field, value, annotation);
                    }
                }
            }
        } catch (Exception e) {
            log.error("构建实体查询条件失败", e);
        }
    }

    /**
     * 添加排序条件
     */
    private static <T> void addOrderByConditions(QueryWrapper<T> wrapper, PageDTO queryDTO) {
        if (queryDTO.getOrderByList() != null && !queryDTO.getOrderByList().isEmpty()) {
            for (PageDTO.OrderBy orderBy : queryDTO.getOrderByList()) {
                if (orderBy.getField() != null && !orderBy.getField().trim().isEmpty()) {
                    String columnName = getColumnName(orderBy.getField());
                    if ("DESC".equalsIgnoreCase(orderBy.getDirection())) {
                        wrapper.orderByDesc(columnName);
                    } else {
                        wrapper.orderByAsc(columnName);
                    }
                }
            }
        }
    }

    /**
     * 添加额外条件的查询条件
     */
    private static <T> void addExtraConditions(QueryWrapper<T> wrapper, Map<String, Object> conditions) {
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value != null) {
                // 支持简单的条件类型推断
                if (value instanceof String && ((String) value).contains("%")) {
                    wrapper.like(getColumnName(key), value);
                } else if (value instanceof Collection) {
                    wrapper.in(getColumnName(key), (Collection<?>) value);
                } else {
                    wrapper.eq(getColumnName(key), value);
                }
            }
        }
    }

    /**
     * 添加单个查询条件
     */
    private static <T> void addCondition(QueryWrapper<T> wrapper, Field field, Object value, QueryCondition annotation) {
        try {
            String fieldName = annotation.field().isEmpty() ? field.getName() : annotation.field();
            
            switch (annotation.type()) {
                case EQ:
                    wrapper.eq(getColumnName(fieldName), value);
                    break;
                case NE:
                    wrapper.ne(getColumnName(fieldName), value);
                    break;
                case LIKE:
                    wrapper.like(getColumnName(fieldName), value);
                    break;
                case LIKE_LEFT:
                    wrapper.likeLeft(getColumnName(fieldName), value);
                    break;
                case LIKE_RIGHT:
                    wrapper.likeRight(getColumnName(fieldName), value);
                    break;
                case GT:
                    wrapper.gt(getColumnName(fieldName), value);
                    break;
                case GE:
                    wrapper.ge(getColumnName(fieldName), value);
                    break;
                case LT:
                    wrapper.lt(getColumnName(fieldName), value);
                    break;
                case LE:
                    wrapper.le(getColumnName(fieldName), value);
                    break;
                case IN:
                    if (value instanceof Collection) {
                        wrapper.in(getColumnName(fieldName), (Collection<?>) value);
                    }
                    break;
                case NOT_IN:
                    if (value instanceof Collection) {
                        wrapper.notIn(getColumnName(fieldName), (Collection<?>) value);
                    }
                    break;
                case BETWEEN:
                    // BETWEEN需要特殊处理，通常需要两个值
                    if (value instanceof Object[] && ((Object[]) value).length == 2) {
                        Object[] range = (Object[]) value;
                        wrapper.between(getColumnName(fieldName), range[0], range[1]);
                    }
                    break;
                case IS_NULL:
                    wrapper.isNull(getColumnName(fieldName));
                    break;
                case IS_NOT_NULL:
                    wrapper.isNotNull(getColumnName(fieldName));
                    break;
            }
        } catch (Exception e) {
            log.error("添加查询条件失败: field={}, value={}", field.getName(), value, e);
        }
    }

    /**
     * 判断是否应该包含该值
     */
    private static boolean shouldIncludeValue(Object value, QueryCondition annotation) {
        if (value == null) {
            return !annotation.ignoreNull();
        }
        
        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.isEmpty() && annotation.ignoreEmpty()) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * 获取列名
     */
    private static String getColumnName(String fieldName) {
        // 简单的驼峰转下划线转换
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
} 