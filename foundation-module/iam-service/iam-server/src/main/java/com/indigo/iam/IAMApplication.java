package com.indigo.iam;

import org.mybatis.spring.annotation.MapperScan;
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
        },
        scanBasePackages = {"com.indigo.iam", "com.indigo.databases", "com.indigo.core"}
)
@EnableDiscoveryClient
@MapperScan("com.indigo.iam.repository.mapper")
public class IAMApplication {
    public static void main(String[] args) {
        SpringApplication.run(IAMApplication.class, args);
    }
}
