package com.indigo.databases.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.indigo.databases.dynamic.DynamicRoutingDataSource;
import com.indigo.databases.enums.DatabaseType;
import com.indigo.databases.enums.PoolType;
import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源自动配置
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class DynamicDataSourceAutoConfiguration {

    private final DynamicDataSourceProperties properties;

    public DynamicDataSourceAutoConfiguration(DynamicDataSourceProperties properties) {
        this.properties = properties;
        log.info("DynamicDataSourceProperties loaded: {}", properties);
    }

    @Bean
    public DynamicRoutingDataSource dynamicDataSource() {
        DynamicRoutingDataSource dynamicDataSource = new DynamicRoutingDataSource(properties);

        // 配置数据源
        properties.getDatasource().forEach((name, props) -> {
            try {
                // 创建数据源 - 不要在这里包裹 DataSourceProxy
                DataSource dataSource = createDataSource(props);
                // 使用 addDataSource 方法添加数据源
                dynamicDataSource.addDataSource(name, dataSource);
                log.info("Initialized datasource [{}] with type [{}] and pool [{}]",
                        name, props.getType(), props.getPoolType());
            } catch (Exception e) {
                log.error("Failed to initialize datasource [{}]: {}", name, e.getMessage());
                throw new RuntimeException("Failed to initialize datasource: " + name, e);
            }
        });

        log.info("Setting default data source: {}", properties.getPrimary());
        
        // 设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(dynamicDataSource.getDataSources().get(properties.getPrimary()));

        return dynamicDataSource;
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("dynamicDataSource") DynamicRoutingDataSource dynamicDataSource) {
        return new DataSourceProxy(dynamicDataSource); // 只包一层
    }
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 创建数据源
     */
    private DataSource createDataSource(DynamicDataSourceProperties.DataSourceProperties props) {
        // 根据连接池类型创建数据源
        DataSource dataSource = switch (props.getPoolType()) {
            case HIKARI -> createHikariDataSource(props);
            case DRUID -> createDruidDataSource(props);
        };

        // 根据数据库类型设置特定配置
        configureDataSourceByType(dataSource, props.getType());

        return dataSource;
    }

    /**
     * 创建HikariCP数据源
     */
    private DataSource createHikariDataSource(DynamicDataSourceProperties.DataSourceProperties props) {
        HikariDataSource dataSource = new HikariDataSource();

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
            log.error("Failed to set Druid filters: {}", e.getMessage());
        }

        return dataSource;
    }

    /**
     * 根据数据库类型配置数据源
     */
    private void configureDataSourceByType(DataSource dataSource, DatabaseType type) {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            switch (type) {
                case MYSQL -> {
                    hikariDataSource.addDataSourceProperty("cachePrepStmts", "true");
                    hikariDataSource.addDataSourceProperty("prepStmtCacheSize", "250");
                    hikariDataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                    hikariDataSource.addDataSourceProperty("useServerPrepStmts", "true");
                }
                case POSTGRESQL -> hikariDataSource.addDataSourceProperty("reWriteBatchedInserts", "true");
                case ORACLE -> hikariDataSource.addDataSourceProperty("oracle.jdbc.fanEnabled", "false");
                case SQLSERVER -> {
                    hikariDataSource.addDataSourceProperty("encrypt", "false");
                    hikariDataSource.addDataSourceProperty("trustServerCertificate", "true");
                }
                case H2 -> hikariDataSource.addDataSourceProperty("MODE", "MySQL");
            }
        } else if (dataSource instanceof DruidDataSource druidDataSource) {
            switch (type) {
                case MYSQL -> {
                    druidDataSource.addConnectionProperty("cachePrepStmts", "true");
                    druidDataSource.addConnectionProperty("prepStmtCacheSize", "250");
                    druidDataSource.addConnectionProperty("prepStmtCacheSqlLimit", "2048");
                    druidDataSource.addConnectionProperty("useServerPrepStmts", "true");
                }
                case POSTGRESQL -> druidDataSource.addConnectionProperty("reWriteBatchedInserts", "true");
                case ORACLE -> druidDataSource.addConnectionProperty("oracle.jdbc.fanEnabled", "false");
                case SQLSERVER -> {
                    druidDataSource.addConnectionProperty("encrypt", "false");
                    druidDataSource.addConnectionProperty("trustServerCertificate", "true");
                }
                case H2 -> druidDataSource.addConnectionProperty("MODE", "MySQL");
            }
        }
    }
} 