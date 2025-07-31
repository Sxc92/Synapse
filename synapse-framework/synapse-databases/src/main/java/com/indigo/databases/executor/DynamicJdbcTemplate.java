package com.indigo.databases.executor;

import com.indigo.databases.dynamic.DynamicDataSourceContextHolder;
import com.indigo.databases.loadbalance.DataSourceLoadBalancer;
import com.indigo.databases.enums.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 动态JdbcTemplate
 * 支持多数据源切换的JdbcTemplate包装器
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@Component
public class DynamicJdbcTemplate {
    
    private final DataSource dataSource;
    
    @Autowired
    private DataSourceLoadBalancer dataSourceLoadBalancer;
    
    public DynamicJdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * 获取当前数据源的JdbcTemplate
     */
    private JdbcTemplate getJdbcTemplate() {
        String currentDataSource = DynamicDataSourceContextHolder.getDataSource();
        log.debug("Using dataSource: {}", currentDataSource);
        
        // 由于使用了动态数据源，JdbcTemplate会自动路由到正确的数据源
        return new JdbcTemplate(dataSource);
    }
    
    /**
     * 获取读数据源（从库轮询）
     */
    private JdbcTemplate getReadJdbcTemplate() {
        // 使用负载均衡器获取从库数据源
        String slaveDataSource = dataSourceLoadBalancer.getDataSource(DataSourceType.SLAVE);
        if (slaveDataSource != null) {
            log.debug("Using slave dataSource: {}", slaveDataSource);
            // 设置数据源上下文
            DynamicDataSourceContextHolder.setDataSource(slaveDataSource);
        }
        return new JdbcTemplate(dataSource);
    }
    
    /**
     * 获取写数据源（主库）
     */
    private JdbcTemplate getWriteJdbcTemplate() {
        // 使用负载均衡器获取主库数据源
        String masterDataSource = dataSourceLoadBalancer.getDataSource(DataSourceType.MASTER);
        if (masterDataSource != null) {
            log.debug("Using master dataSource: {}", masterDataSource);
            // 设置数据源上下文
            DynamicDataSourceContextHolder.setDataSource(masterDataSource);
        }
        return new JdbcTemplate(dataSource);
    }
    
    /**
     * 查询列表（使用从库轮询）
     */
    public List<Map<String, Object>> queryForList(String sql, Object... args) {
        return getReadJdbcTemplate().queryForList(sql, args);
    }
    
    /**
     * 查询单个对象（使用从库轮询）
     */
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
        return getReadJdbcTemplate().queryForObject(sql, requiredType, args);
    }
    
    /**
     * 查询单个Map（使用从库轮询）
     */
    public Map<String, Object> queryForMap(String sql, Object... args) {
        return getReadJdbcTemplate().queryForMap(sql, args);
    }
    
    /**
     * 查询列表（使用RowMapper，从库轮询）
     */
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        return getReadJdbcTemplate().query(sql, rowMapper, args);
    }
    
    /**
     * 查询单个对象（使用RowMapper，从库轮询）
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        return getReadJdbcTemplate().queryForObject(sql, rowMapper, args);
    }
    
    /**
     * 更新操作（使用主库）
     */
    public int update(String sql, Object... args) {
        return getWriteJdbcTemplate().update(sql, args);
    }
    
    /**
     * 批量更新（使用主库）
     */
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        return getWriteJdbcTemplate().batchUpdate(sql, batchArgs);
    }
} 