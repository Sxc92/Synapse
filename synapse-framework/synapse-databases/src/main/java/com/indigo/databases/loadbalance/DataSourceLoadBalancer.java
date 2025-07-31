package com.indigo.databases.loadbalance;

import com.indigo.databases.dynamic.DynamicRoutingDataSource;
import com.indigo.databases.enums.DataSourceType;
import com.indigo.databases.health.DataSourceHealthChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 数据源负载均衡器
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Slf4j
@Component
public class DataSourceLoadBalancer {
    
    private final DynamicRoutingDataSource routingDataSource;
    private final DataSourceHealthChecker healthChecker;
    private final AtomicInteger counter = new AtomicInteger(0);
    
    public DataSourceLoadBalancer(@Qualifier("dynamicDataSource") DynamicRoutingDataSource routingDataSource,
                                DataSourceHealthChecker healthChecker) {
        this.routingDataSource = routingDataSource;
        this.healthChecker = healthChecker;
    }
    
    /**
     * 获取数据源
     *
     * @param type 数据源类型
     * @return 数据源名称
     */
    public String getDataSource(DataSourceType type) {
        // 确保健康检查已完成
        if (!healthChecker.isInitialized()) {
            healthChecker.checkHealthAndWait();
        }
        
        Map<String, DataSource> dataSources = routingDataSource.getDataSources();
        log.debug("Available data sources: {}", dataSources.keySet());
        
        // 过滤出指定类型且健康的数据源
        List<String> availableDataSources = dataSources.keySet().stream()
                .filter(name -> {
                    // 根据数据源名称判断类型
                    boolean isType = switch (type) {
                        case MASTER -> name.startsWith("master");
                        case SLAVE -> name.startsWith("slave");
                    };
                    boolean isHealthy = healthChecker.isHealthy(name);
                    log.debug("DataSource [{}] - isType: {}, isHealthy: {}", name, isType, isHealthy);
                    return isType && isHealthy;
                })
                .toList();
        
        log.debug("Available {} data sources: {}", type, availableDataSources);
        
        if (availableDataSources.isEmpty()) {
            // 如果没有可用的数据源，尝试使用主数据源
            String primary = routingDataSource.getCurrentDataSource();
            if (primary != null && healthChecker.isHealthy(primary)) {
                log.warn("No available {} data source found, using primary data source: {}", type, primary);
                return primary;
            }
            // 如果主数据源也不可用，返回null
            log.error("No available data source found for type: {}", type);
            return null;
        }
        
        // 使用轮询策略选择数据源
        int index = counter.getAndIncrement() % availableDataSources.size();
        if (counter.get() > 10000) {
            counter.set(0);
        }
        
        String selectedDataSource = availableDataSources.get(index);
        log.debug("Selected {} data source: {}", type, selectedDataSource);
        return selectedDataSource;
    }
    
    /**
     * 获取所有可用的数据源
     *
     * @param type 数据源类型
     * @return 可用的数据源列表
     */
    public List<String> getAvailableDataSources(DataSourceType type) {
        Map<String, DataSource> dataSources = routingDataSource.getDataSources();
        return dataSources.keySet().stream()
                .filter(name -> {
                    boolean isType = switch (type) {
                        case MASTER -> name.startsWith("master");
                        case SLAVE -> name.startsWith("slave");
                    };
                    return isType && healthChecker.isHealthy(name);
                })
                .collect(Collectors.toList());
    }
} 