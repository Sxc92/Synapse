package com.indigo.databases.config;

import com.indigo.databases.enums.DatabaseType;
import com.indigo.databases.enums.PoolType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 动态数据源配置属性
 *
 * @author 史偕成
 * @date 2024/03/21
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource.dynamic")
public class DynamicDataSourceProperties {
    
    /**
     * 默认数据源
     */
    private String primary = "master";
    
    /**
     * 是否启用严格模式，严格模式下未匹配到数据源直接报错
     */
    private Boolean strict = false;
    
    /**
     * 是否启用seata分布式事务
     */
    private Boolean seata = false;
    
    /**
     * 是否启用p6spy
     */
    private Boolean p6spy = false;
    
    /**
     * 数据源配置
     */
    private Map<String, DataSourceProperties> datasource = new LinkedHashMap<>();
    
    /**
     * 数据源属性
     */
    @Data
    public static class DataSourceProperties {
        /**
         * 数据库类型
         */
        private DatabaseType type;
        
        /**
         * 连接池类型 (默认HikariCP)
         */
        private PoolType poolType = PoolType.HIKARI;
        
        /**
         * 主机地址
         */
        private String host;
        
        /**
         * 端口号
         */
        private Integer port;
        
        /**
         * 数据库名
         */
        private String database;
        
        /**
         * 用户名
         */
        private String username;
        
        /**
         * 密码
         */
        private String password;
        
        /**
         * 连接参数
         */
        private Map<String, String> params = new LinkedHashMap<>();
        
        /**
         * HikariCP连接池配置
         */
        private HikariPoolProperties hikari = new HikariPoolProperties();
        
        /**
         * Druid连接池配置
         */
        private DruidPoolProperties druid = new DruidPoolProperties();
        
        /**
         * 获取完整的JDBC URL
         */
        public String getUrl() {
            StringBuilder url = new StringBuilder(type.getUrlPrefix());
            
            // 添加主机和端口
            url.append(host);
            if (port != null) {
                url.append(":").append(port);
            }
            
            // 添加数据库名
            if (database != null && !database.isEmpty()) {
                if (type == DatabaseType.ORACLE) {
                    url.append(":").append(database);
                } else {
                    url.append("/").append(database);
                }
            }
            
            // 添加连接参数
            if (!params.isEmpty()) {
                url.append("?");
                params.forEach((key, value) -> url.append(key).append("=").append(value).append("&"));
                url.setLength(url.length() - 1); // 删除最后一个&
            }
            
            return url.toString();
        }
        
        /**
         * 获取驱动类名
         */
        public String getDriverClassName() {
            return type.getDriverClassName();
        }
    }
    
    /**
     * HikariCP连接池配置属性
     */
    @Data
    public static class HikariPoolProperties {
        /**
         * 连接池最小空闲连接数
         */
        private Integer minimumIdle = 5;
        
        /**
         * 连接池最大连接数
         */
        private Integer maximumPoolSize = 15;
        
        /**
         * 空闲连接最大存活时间，默认600000(10分钟)
         */
        private Long idleTimeout = 600000L;
        
        /**
         * 连接最大存活时间，默认1800000(30分钟)
         */
        private Long maxLifetime = 1800000L;
        
        /**
         * 连接超时时间，默认30000(30秒)
         */
        private Long connectionTimeout = 30000L;
        
        /**
         * 连接测试查询
         */
        private String connectionTestQuery = "SELECT 1";
    }
    
    /**
     * Druid连接池配置属性
     */
    @Data
    public static class DruidPoolProperties {
        /**
         * 初始连接数
         */
        private Integer initialSize = 5;
        
        /**
         * 最小连接池数量
         */
        private Integer minIdle = 5;
        
        /**
         * 最大连接池数量
         */
        private Integer maxActive = 20;
        
        /**
         * 获取连接等待超时时间
         */
        private Long maxWait = 60000L;
        
        /**
         * 检测间隔时间，检测需要关闭的空闲连接
         */
        private Long timeBetweenEvictionRunsMillis = 60000L;
        
        /**
         * 连接在池中最小生存的时间
         */
        private Long minEvictableIdleTimeMillis = 300000L;
        
        /**
         * 连接在池中最大生存的时间
         */
        private Long maxEvictableIdleTimeMillis = 900000L;
        
        /**
         * 检测连接是否有效
         */
        private String validationQuery = "SELECT 1";
        
        /**
         * 建议配置为true，不影响性能，并且保证安全性
         */
        private Boolean testWhileIdle = true;
        
        /**
         * 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
         */
        private Boolean testOnBorrow = false;
        
        /**
         * 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
         */
        private Boolean testOnReturn = false;
        
        /**
         * 是否缓存preparedStatement，也就是PSCache
         */
        private Boolean poolPreparedStatements = true;
        
        /**
         * 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true
         */
        private Integer maxPoolPreparedStatementPerConnectionSize = 20;
        
        /**
         * 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
         */
        private String filters = "stat,wall";
    }
} 