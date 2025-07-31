package com.indigo.databases.interceptor;

import com.indigo.databases.dynamic.DynamicDataSourceContextHolder;
import com.indigo.databases.enums.DataSourceType;
import com.indigo.databases.loadbalance.DataSourceLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

import java.util.Properties;

/**
 * 自动数据源切换拦截器
 * 根据SQL类型自动切换数据源：
 * - SELECT语句使用从库
 * - INSERT/UPDATE/DELETE语句使用主库
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, org.apache.ibatis.session.RowBounds.class, org.apache.ibatis.session.ResultHandler.class})
})
public class AutoDataSourceInterceptor implements Interceptor, InnerInterceptor {

    private final DataSourceLoadBalancer loadBalancer;

    public AutoDataSourceInterceptor(DataSourceLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        
        // 根据SQL类型选择数据源
        String dataSource;
        if (sqlCommandType == SqlCommandType.SELECT) {
            // 读操作使用从库
            dataSource = loadBalancer.getDataSource(DataSourceType.SLAVE);
            log.info("SQL类型: {}, 使用从库数据源: {}", sqlCommandType, dataSource);
        } else {
            // 写操作使用主库
            dataSource = loadBalancer.getDataSource(DataSourceType.MASTER);
            log.info("SQL类型: {}, 使用主库数据源: {}", sqlCommandType, dataSource);
        }
        
        // 如果获取不到数据源，使用默认数据源（不设置，让系统使用默认的）
        if (dataSource == null) {
            log.warn("No available data source found for {} operation, using default data source", sqlCommandType);
            return invocation.proceed();
        }
        
        // 设置数据源
        DynamicDataSourceContextHolder.setDataSource(dataSource);
        log.info("数据源已切换为: {}", dataSource);
        
        try {
            return invocation.proceed();
        } finally {
            // 清除数据源
            String previousDataSource = DynamicDataSourceContextHolder.getDataSource();
            DynamicDataSourceContextHolder.clear();
            log.info("数据源已清除，之前的数据源: {}", previousDataSource);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以在这里设置一些属性
    }
} 