package com.indigo.synapse.databases;

import com.alibaba.druid.pool.DruidDataSource;
import com.indigo.databases.config.DynamicDataSourceProperties;
import com.indigo.databases.dynamic.DynamicRoutingDataSource;
import com.indigo.databases.health.DataSourceHealthChecker;
import com.indigo.databases.loadbalance.DataSourceLoadBalancer;
import com.indigo.databases.interceptor.AutoDataSourceInterceptor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Qualifier;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration
@Profile("test")
public class TestConfig {
    // Test configuration class

    @Bean
    @Primary
    public DataSource dynamicDataSource(DynamicDataSourceProperties properties) {
        DynamicRoutingDataSource dynamicDataSource = new DynamicRoutingDataSource(properties);
        
        // 配置数据源
        Map<Object, Object> dataSourceMap = new HashMap<>();
        properties.getDatasource().forEach((name, props) -> {
            try {
                // 创建数据源
                DataSource dataSource = createDataSource(props);
                dynamicDataSource.addDataSource(name, dataSource);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize datasource: " + name, e);
            }
        });
        
        // 设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(dynamicDataSource.getDataSources().get(properties.getPrimary()));
        
        return dynamicDataSource;
    }

    // 注册数据源健康检查器
    @Bean
    public DataSourceHealthChecker dataSourceHealthChecker(@Qualifier("dynamicDataSource") DataSource dataSource) {
        return new DataSourceHealthChecker((DynamicRoutingDataSource) dataSource);
    }

    // 注册数据源负载均衡器
    @Bean
    public DataSourceLoadBalancer dataSourceLoadBalancer(@Qualifier("dynamicDataSource") DataSource dataSource, DataSourceHealthChecker healthChecker) {
        return new DataSourceLoadBalancer((DynamicRoutingDataSource) dataSource, healthChecker);
    }

    // 注册自动数据源拦截器
    @Bean
    public AutoDataSourceInterceptor autoDataSourceInterceptor(DataSourceLoadBalancer loadBalancer) {
        return new AutoDataSourceInterceptor(loadBalancer);
    }

    /**
     * 创建数据源
     */
    private DataSource createDataSource(DynamicDataSourceProperties.DataSourceProperties props) {
        // 根据连接池类型创建数据源
        return switch (props.getPoolType()) {
            case HIKARI -> createHikariDataSource(props);
            case DRUID -> createDruidDataSource(props);
        };
    }

    /**
     * 创建HikariCP数据源
     */
    private DataSource createHikariDataSource(DynamicDataSourceProperties.DataSourceProperties props) {
        com.zaxxer.hikari.HikariDataSource dataSource = new com.zaxxer.hikari.HikariDataSource();
        
        // 设置基本属性
        dataSource.setJdbcUrl(props.getUrl());
        dataSource.setUsername(props.getUsername());
        dataSource.setPassword(props.getPassword());
        dataSource.setDriverClassName(props.getDriverClassName());
        
        // 设置连接池属性
        DynamicDataSourceProperties.HikariPoolProperties hikari = props.getHikari();
        dataSource.setMinimumIdle(hikari.getMinimumIdle());
        dataSource.setMaximumPoolSize(hikari.getMaximumPoolSize());
        dataSource.setIdleTimeout(hikari.getIdleTimeout());
        dataSource.setMaxLifetime(hikari.getMaxLifetime());
        dataSource.setConnectionTimeout(hikari.getConnectionTimeout());
        dataSource.setConnectionTestQuery(hikari.getConnectionTestQuery());
        
        return dataSource;
    }

    /**
     * 创建Druid数据源
     */
    private DataSource createDruidDataSource(DynamicDataSourceProperties.DataSourceProperties props) {
        DruidDataSource dataSource = new DruidDataSource();
        
        // 设置基本属性
        dataSource.setUrl(props.getUrl());
        dataSource.setUsername(props.getUsername());
        dataSource.setPassword(props.getPassword());
        dataSource.setDriverClassName(props.getDriverClassName());
        
        // 设置连接池属性
        DynamicDataSourceProperties.DruidPoolProperties druid = props.getDruid();
        dataSource.setInitialSize(druid.getInitialSize());
        dataSource.setMinIdle(druid.getMinIdle());
        dataSource.setMaxActive(druid.getMaxActive());
        dataSource.setMaxWait(druid.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(druid.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(druid.getMinEvictableIdleTimeMillis());
        dataSource.setMaxEvictableIdleTimeMillis(druid.getMaxEvictableIdleTimeMillis());
        dataSource.setValidationQuery(druid.getValidationQuery());
        dataSource.setTestWhileIdle(druid.getTestWhileIdle());
        dataSource.setTestOnBorrow(druid.getTestOnBorrow());
        dataSource.setTestOnReturn(druid.getTestOnReturn());
        dataSource.setPoolPreparedStatements(druid.getPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(druid.getMaxPoolPreparedStatementPerConnectionSize());
        
        try {
            dataSource.setFilters(druid.getFilters());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set Druid filters", e);
        }
        
        return dataSource;
    }
} 