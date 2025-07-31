package com.indigo.databases.config;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.indigo.core.context.UserContext;
import com.indigo.databases.dynamic.DynamicDataSourceContextHolder;
import com.indigo.databases.enums.DatabaseType;
import com.indigo.databases.interceptor.AutoDataSourceInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus配置
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {

    private final AutoDataSourceInterceptor autoDataSourceInterceptor;
    private final DynamicDataSourceProperties dynamicDataSourceProperties;

    public MybatisPlusConfig(AutoDataSourceInterceptor autoDataSourceInterceptor,
                             DynamicDataSourceProperties dynamicDataSourceProperties) {
        this.autoDataSourceInterceptor = autoDataSourceInterceptor;
        this.dynamicDataSourceProperties = dynamicDataSourceProperties;
        log.info("MybatisPlusConfig 已加载");
    }

    /**
     * 配置MyBatis-Plus插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件 - 使用动态数据库类型
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        paginationInterceptor.setDbType(getCurrentDbType());
        interceptor.addInnerInterceptor(paginationInterceptor);
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 防止全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        // 自动数据源切换拦截器
        interceptor.addInnerInterceptor(autoDataSourceInterceptor);

        return interceptor;
    }

    /**
     * 获取当前数据源对应的数据库类型
     */
    private DbType getCurrentDbType() {
        String currentDataSource = DynamicDataSourceContextHolder.getDataSource();
        if (currentDataSource == null) {
            currentDataSource = dynamicDataSourceProperties.getPrimary();
        }

        DatabaseType databaseType = dynamicDataSourceProperties.getDatasource()
                .get(currentDataSource)
                .getType();

        return switch (databaseType) {
            case MYSQL -> DbType.MYSQL;
            case POSTGRESQL -> DbType.POSTGRE_SQL;
            case ORACLE -> DbType.ORACLE;
            case SQLSERVER -> DbType.SQL_SERVER;
            case H2 -> DbType.H2;
        };
    }

    /**
     * 配置MyBatis-Plus全局配置
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        // 配置数据库相关
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        globalConfig.setDbConfig(dbConfig);
        return globalConfig;
    }
    
    /**
     * 手动注册 MetaObjectHandler Bean
     */
    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public MetaObjectHandler metaObjectHandler() {
        log.info("手动注册 MetaObjectHandler Bean");
        return new MyMetaObjectHandler();
    }
}

/**
 * MyBatis-Plus 元数据处理器
 * 用于自动填充创建时间、更新时间、创建人、更新人等字段
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Slf4j
@Component
@ConditionalOnMissingBean(MyMetaObjectHandler.class)
class MyMetaObjectHandler implements MetaObjectHandler {
    
    public MyMetaObjectHandler() {
        log.info("MyMetaObjectHandler Bean 已创建");
    }
    
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        UserContext currentUser = UserContext.getCurrentUser();
        log.info("执行插入填充: 当前时间={}", now);
        log.info("执行插入填充: 当前user={}", currentUser);
        // 临时测试：只填充时间字段
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "modifyTime", LocalDateTime.class, now);
        
        // 临时硬编码测试用户信息
//        String currentUserId = "test-user-123";
//        String tenantId = "test-tenant-456";
        
        this.strictInsertFill(metaObject, "createUser", String.class, currentUser.getUserId());
        this.strictInsertFill(metaObject, "modifyUser", String.class, currentUser.getUserId());
        this.strictInsertFill(metaObject, "tenantId", String.class, currentUser.getTenantId());
        
        log.info("插入填充完成");
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        
        log.info("执行更新填充: 当前时间={}", now);
        
        // 临时测试：只填充时间字段
        this.strictUpdateFill(metaObject, "modifyTime", LocalDateTime.class, now);
        
        // 临时硬编码测试用户信息
        String currentUserId = "test-user-123";
        this.strictUpdateFill(metaObject, "modifyUser", String.class, currentUserId);
        
        log.info("更新填充完成");
    }
} 