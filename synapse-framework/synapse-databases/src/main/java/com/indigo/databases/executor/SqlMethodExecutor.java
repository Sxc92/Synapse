package com.indigo.databases.executor;

import com.indigo.databases.annotation.SqlPage;
import com.indigo.databases.annotation.SqlQuery;
import com.indigo.databases.annotation.SqlUpdate;
import com.indigo.databases.dto.PageDTO;
import com.indigo.databases.dto.PageResult;
import com.indigo.databases.dynamic.DynamicDataSourceContextHolder;
import com.indigo.databases.loadbalance.DataSourceLoadBalancer;
import com.indigo.databases.enums.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SQL方法执行器
 * 负责执行注解SQL方法
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@Component
public class SqlMethodExecutor {
    
    @Autowired
    private DynamicJdbcTemplate dynamicJdbcTemplate;
    
    @Autowired
    private DataSourceLoadBalancer dataSourceLoadBalancer;
    
    /**
     * 执行查询方法
     */
    public Object executeQuery(Method method, Object[] args) {
        SqlQuery sqlQuery = method.getAnnotation(SqlQuery.class);
        if (sqlQuery == null) {
            throw new IllegalArgumentException("Method must be annotated with @SqlQuery");
        }
        
        String sql = sqlQuery.value();
        Map<String, Object> params = parseParameters(method, args);
        
        log.debug("Executing SQL query: {}, params: {}, dataSource: {}", 
                 sql, params, DynamicDataSourceContextHolder.getDataSource());
        
        try {
            Class<?> returnType = method.getReturnType();
            
            if (List.class.isAssignableFrom(returnType)) {
                // 返回列表
                return executeQueryForList(sql, params, getGenericType(method));
            } else if (returnType.isPrimitive() || Number.class.isAssignableFrom(returnType)) {
                // 返回数字
                return executeQueryForNumber(sql, params, returnType);
            } else {
                // 返回单个对象
                return executeQueryForObject(sql, params, returnType);
            }
        } catch (Exception e) {
            log.error("Error executing SQL query: {}", sql, e);
            throw new RuntimeException("SQL execution failed", e);
        }
    }
    
    /**
     * 执行更新方法
     */
    public Object executeUpdate(Method method, Object[] args) {
        SqlUpdate sqlUpdate = method.getAnnotation(SqlUpdate.class);
        if (sqlUpdate == null) {
            throw new IllegalArgumentException("Method must be annotated with @SqlUpdate");
        }
        
        String sql = sqlUpdate.value();
        Map<String, Object> params = parseParameters(method, args);
        
        log.debug("Executing SQL update: {}, params: {}, dataSource: {}", 
                 sql, params, DynamicDataSourceContextHolder.getDataSource());
        
        try {
            int rows = dynamicJdbcTemplate.update(sql, params.values().toArray());
            
            Class<?> returnType = method.getReturnType();
            if (returnType == int.class || returnType == Integer.class) {
                return rows;
            } else if (returnType == boolean.class || returnType == Boolean.class) {
                return rows > 0;
            } else {
                return rows;
            }
        } catch (Exception e) {
            log.error("Error executing SQL update: {}", sql, e);
            throw new RuntimeException("SQL execution failed", e);
        }
    }
    
    /**
     * 执行分页查询方法
     */
    public Object executePage(Method method, Object[] args) {
        SqlPage sqlPage = method.getAnnotation(SqlPage.class);
        if (sqlPage == null) {
            throw new IllegalArgumentException("Method must be annotated with @SqlPage");
        }
        
        String countSql = sqlPage.countSql();
        String dataSql = sqlPage.dataSql();
        Map<String, Object> params = parseParameters(method, args);
        
        // 提取分页参数
        PageDTO pageDTO = extractPageDTO(args);
        if (pageDTO != null) {
            params.put("pageNum", pageDTO.getPageNo());
            params.put("pageSize", pageDTO.getPageSize());
            
            // 添加排序
            if (pageDTO.getOrderByList() != null && !pageDTO.getOrderByList().isEmpty()) {
                String orderBy = pageDTO.getOrderByList().stream()
                    .map(order -> order.getField() + " " + order.getDirection())
                    .collect(Collectors.joining(", "));
                params.put("orderBy", orderBy);
            }
        }
        
        log.debug("Executing SQL page query: countSql={}, dataSql={}, params={}, dataSource={}", 
                 countSql, dataSql, params, DynamicDataSourceContextHolder.getDataSource());
        
        try {
            // 执行计数查询
            Long total = dynamicJdbcTemplate.queryForObject(countSql, Long.class, params.values().toArray());
            if (total == null) {
                total = 0L;
            }
            
            // 执行数据查询
            List<?> records = executeQueryForList(dataSql, params, getGenericType(method));
            
            // 构建分页结果
            return new PageResult<>(
                records,
                total,
                (long) pageDTO.getPageNo(),
                (long) pageDTO.getPageSize()
            );
        } catch (Exception e) {
            log.error("Error executing SQL page query: countSql={}, dataSql={}", countSql, dataSql, e);
            throw new RuntimeException("SQL execution failed", e);
        }
    }
    
    /**
     * 解析方法参数
     */
    private Map<String, Object> parseParameters(Method method, Object[] args) {
        Map<String, Object> params = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object value = args[i];
            
            com.indigo.databases.annotation.Param paramAnnotation = 
                parameter.getAnnotation(com.indigo.databases.annotation.Param.class);
            
            String paramName = paramAnnotation != null ? paramAnnotation.value() : parameter.getName();
            params.put(paramName, value);
        }
        
        return params;
    }
    
    /**
     * 提取分页参数
     */
    private PageDTO extractPageDTO(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof PageDTO) {
                return (PageDTO) arg;
            }
        }
        return null;
    }
    
    /**
     * 获取泛型类型
     */
    private Class<?> getGenericType(Method method) {
        try {
            return (Class<?>) method.getGenericReturnType();
        } catch (Exception e) {
            return Object.class;
        }
    }
    
    /**
     * 执行查询返回列表
     */
    private List<?> executeQueryForList(String sql, Map<String, Object> params, Class<?> elementType) {
        if (elementType == Map.class) {
            return dynamicJdbcTemplate.queryForList(sql, params.values().toArray());
        } else {
            RowMapper<?> rowMapper = new BeanPropertyRowMapper<>(elementType);
            return dynamicJdbcTemplate.query(sql, rowMapper, params.values().toArray());
        }
    }
    
    /**
     * 执行查询返回单个对象
     */
    private Object executeQueryForObject(String sql, Map<String, Object> params, Class<?> returnType) {
        if (returnType == Map.class) {
            return dynamicJdbcTemplate.queryForMap(sql, params.values().toArray());
        } else {
            RowMapper<?> rowMapper = new BeanPropertyRowMapper<>(returnType);
            return dynamicJdbcTemplate.queryForObject(sql, rowMapper, params.values().toArray());
        }
    }
    
    /**
     * 执行查询返回数字
     */
    private Object executeQueryForNumber(String sql, Map<String, Object> params, Class<?> returnType) {
        return dynamicJdbcTemplate.queryForObject(sql, returnType, params.values().toArray());
    }
    
    /**
     * 简单的Bean属性行映射器
     */
    private static class BeanPropertyRowMapper<T> implements RowMapper<T> {
        private final Class<T> mappedClass;
        
        public BeanPropertyRowMapper(Class<T> mappedClass) {
            this.mappedClass = mappedClass;
        }
        
        @Override
        public T mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            try {
                T instance = mappedClass.getDeclaredConstructor().newInstance();
                java.sql.ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    
                    try {
                        java.lang.reflect.Field field = mappedClass.getDeclaredField(columnName);
                        field.setAccessible(true);
                        field.set(instance, value);
                    } catch (NoSuchFieldException e) {
                        // 忽略不存在的字段
                    }
                }
                
                return instance;
            } catch (Exception e) {
                throw new RuntimeException("Error mapping row to " + mappedClass, e);
            }
        }
    }
} 