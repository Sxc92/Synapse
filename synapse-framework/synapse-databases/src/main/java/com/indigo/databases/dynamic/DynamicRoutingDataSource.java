package com.indigo.databases.dynamic;

import com.indigo.databases.config.DynamicDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态路由数据源
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    
    private final DynamicDataSourceProperties properties;
    private final Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();
    private final Map<Object, Object> resolvedDataSources = new ConcurrentHashMap<>();
    
    public DynamicRoutingDataSource(DynamicDataSourceProperties properties) {
        this.properties = properties;
        // 设置默认数据源
        setDefaultTargetDataSource(null);
        // 设置数据源映射
        setTargetDataSources(resolvedDataSources);
    }
    
    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceName = DynamicDataSourceContextHolder.getDataSource();
        if (StringUtils.hasText(dataSourceName)) {
            log.info("当前数据源路由: [{}]", dataSourceName);
            return dataSourceName;
        }
        
        // 返回默认数据源
        String primary = properties.getPrimary();
        log.info("使用默认数据源: [{}]", primary);
        return primary;
    }
    
    @Override
    protected DataSource determineTargetDataSource() {
        String lookupKey = (String) determineCurrentLookupKey();
        log.info("查找数据源，key: [{}], 可用keys: {}", lookupKey, resolvedDataSources.keySet());
        
        DataSource dataSource = (DataSource) resolvedDataSources.get(lookupKey);
        if (dataSource == null && properties.getStrict()) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        }
        // 如果找不到指定数据源，返回默认数据源
        if (dataSource == null) {
            dataSource = (DataSource) getResolvedDefaultDataSource();
            log.warn("数据源未找到，key: [{}], 使用默认数据源", lookupKey);
        } else {
            log.info("数据源查找成功，key: [{}], 数据源类型: {}", lookupKey, dataSource.getClass().getSimpleName());
        }
        return dataSource;
    }
    
    /**
     * 添加数据源
     *
     * @param name 数据源名称
     * @param dataSource 数据源
     */
    public void addDataSource(String name, DataSource dataSource) {
        dataSourceMap.put(name, dataSource);
        resolvedDataSources.put(name, dataSource);
        setTargetDataSources(resolvedDataSources);
        afterPropertiesSet();
    }
    
    /**
     * 移除数据源
     *
     * @param name 数据源名称
     */
    public void removeDataSource(String name) {
        dataSourceMap.remove(name);
        resolvedDataSources.remove(name);
        setTargetDataSources(resolvedDataSources);
        afterPropertiesSet();
    }
    
    /**
     * 获取所有数据源
     *
     * @return 数据源映射
     */
    public Map<String, DataSource> getDataSources() {
        // 返回 resolvedDataSources 的内容，与 determineTargetDataSource 保持一致
        Map<String, DataSource> result = new ConcurrentHashMap<>();
        resolvedDataSources.forEach((k, v) -> {
            if (v instanceof DataSource ds) {
                result.put(String.valueOf(k), ds);
            }
        });
        return result;
    }
    
    /**
     * 获取当前数据源
     *
     * @return 当前数据源名称
     */
    public String getCurrentDataSource() {
        return DynamicDataSourceContextHolder.getDataSource();
    }
} 