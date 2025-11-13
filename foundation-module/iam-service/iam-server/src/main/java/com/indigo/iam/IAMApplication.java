package com.indigo.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

@SpringBootApplication(
        exclude = {
                DataSourceAutoConfiguration.class,
                DruidDataSourceAutoConfigure.class,
                JacksonAutoConfiguration.class
                // 注意：RedisAutoConfiguration 已在框架层面排除（synapse-cache 模块的 AutoConfiguration.imports）
        },
        scanBasePackages = {"com.indigo.iam", "com.indigo.databases", "com.indigo.core"}
)
@EnableDiscoveryClient
// 注意：@MapperScan 已移除，因为框架级别的 MybatisPlusConfig 已经配置了全局扫描
// @MapperScan("com.indigo.**.repository.mapper") 在 MybatisPlusConfig 中统一配置
public class IAMApplication {
    public static void main(String[] args) {
        SpringApplication.run(IAMApplication.class, args);
    }
}
