package com.indigo.databases.health;

import com.indigo.databases.dynamic.DynamicRoutingDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源健康检查器
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Slf4j
@Component
public class DataSourceHealthChecker {
    
    private final DynamicRoutingDataSource routingDataSource;
    private final Map<String, Boolean> dataSourceHealth = new ConcurrentHashMap<>();
    /**
     * -- GETTER --
     *  检查是否已初始化
     *
     * @return 是否已初始化
     */
    @Getter
    private volatile boolean initialized = false;
    
    public DataSourceHealthChecker(@Qualifier("dynamicDataSource") DynamicRoutingDataSource routingDataSource) {
        this.routingDataSource = routingDataSource;
    }
    
    /**
     * 初始化时执行一次健康检查
     */
    @PostConstruct
    public void init() {
        checkHealth();
        initialized = true;
    }
    
    /**
     * 每30秒检查一次数据源健康状态
     */
    @Scheduled(fixedRate = 30000)
    public void checkHealth() {
        Map<String, DataSource> dataSources = routingDataSource.getDataSources();
        dataSources.forEach((name, dataSource) -> {
            try {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                dataSourceHealth.put(name, true);
                log.debug("DataSource [{}] is healthy", name);
            } catch (Exception e) {
                dataSourceHealth.put(name, false);
                log.error("DataSource [{}] is unhealthy: {}", name, e.getMessage());
            }
        });
    }
    
    /**
     * 手动触发健康检查并等待完成
     */
    public void checkHealthAndWait() {
        checkHealth();
        // 等待健康检查完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 检查数据源是否健康
     *
     * @param dataSourceName 数据源名称
     * @return 是否健康
     */
    public boolean isHealthy(String dataSourceName) {
        if (dataSourceName == null || dataSourceName.trim().isEmpty()) {
            log.warn("DataSource name is null or empty, returning false");
            return false;
        }
        
        if (!initialized) {
            checkHealthAndWait();
        }
        return dataSourceHealth.getOrDefault(dataSourceName, false);
    }
    
    /**
     * 获取所有数据源的健康状态
     *
     * @return 数据源健康状态映射
     */
    public Map<String, Boolean> getHealthStatus() {
        if (!initialized) {
            checkHealthAndWait();
        }
        return new ConcurrentHashMap<>(dataSourceHealth);
    }

} 