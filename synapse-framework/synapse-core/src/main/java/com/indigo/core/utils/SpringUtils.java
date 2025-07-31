package com.indigo.core.utils;

import com.indigo.core.exception.SpringException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Spring 工具类，用于获取 Spring 上下文和 Bean
 * 
 * @author 史偕成
 * @date 2024/03/21
 **/
@Component
@Lazy(false)
public class SpringUtils implements ApplicationContextAware, DisposableBean {
    
    private static ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }
    
    @Override
    public void destroy() {
        applicationContext = null;
    }
    
    /**
     * 获取 ApplicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        return applicationContext;
    }
    
    /**
     * 获取 Bean
     *
     * @param name Bean 名称
     * @return Bean 实例
     */
    public static Object getBean(String name) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (name == null || name.isEmpty()) {
            throw new SpringException("system.error.bean.name.empty");
        }
        return applicationContext.getBean(name);
    }
    
    /**
     * 获取 Bean
     *
     * @param requiredType Bean 类型
     * @param <T> Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> requiredType) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (requiredType == null) {
            throw new SpringException("system.error.bean.type.null");
        }
        return applicationContext.getBean(requiredType);
    }
    
    /**
     * 获取 Bean
     *
     * @param name Bean 名称
     * @param requiredType Bean 类型
     * @param <T> Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (name == null || name.isEmpty()) {
            throw new SpringException("system.error.bean.name.empty");
        }
        if (requiredType == null) {
            throw new SpringException("system.error.bean.type.null");
        }
        return applicationContext.getBean(name, requiredType);
    }
    
    /**
     * 获取当前环境
     *
     * @return 当前环境
     */
    public static String getActiveProfile() {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        return activeProfiles.length > 0 ? activeProfiles[0] : null;
    }
    
    /**
     * 获取当前环境是否为开发环境
     *
     * @return 是否为开发环境
     */
    public static boolean isDev() {
        return "dev".equals(getActiveProfile());
    }
    
    /**
     * 获取当前环境是否为测试环境
     *
     * @return 是否为测试环境
     */
    public static boolean isTest() {
        return "test".equals(getActiveProfile());
    }
    
    /**
     * 获取当前环境是否为生产环境
     *
     * @return 是否为生产环境
     */
    public static boolean isProd() {
        return "prod".equals(getActiveProfile());
    }
    
    /**
     * 发布事件
     *
     * @param event 事件
     */
    public static void publishEvent(ApplicationEvent event) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (event == null) {
            throw new SpringException("system.error.event.null");
        }
        applicationContext.publishEvent(event);
    }
    
    /**
     * 获取配置属性
     *
     * @param key 配置键
     * @return 配置值
     */
    public static String getProperty(String key) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (key == null || key.isEmpty()) {
            throw new SpringException("system.error.property.key.empty");
        }
        return applicationContext.getEnvironment().getProperty(key);
    }
    
    /**
     * 获取配置属性
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static String getProperty(String key, String defaultValue) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (key == null || key.isEmpty()) {
            throw new SpringException("system.error.property.key.empty");
        }
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }
    
    /**
     * 获取配置属性
     *
     * @param key 配置键
     * @param targetType 目标类型
     * @param <T> 目标类型
     * @return 配置值
     */
    public static <T> T getProperty(String key, Class<T> targetType) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (key == null || key.isEmpty()) {
            throw new SpringException("system.error.property.key.empty");
        }
        if (targetType == null) {
            throw new SpringException("system.error.property.type.null");
        }
        return applicationContext.getEnvironment().getProperty(key, targetType);
    }
    
    /**
     * 获取配置属性
     *
     * @param key 配置键
     * @param targetType 目标类型
     * @param defaultValue 默认值
     * @param <T> 目标类型
     * @return 配置值
     */
    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (key == null || key.isEmpty()) {
            throw new SpringException("system.error.property.key.empty");
        }
        if (targetType == null) {
            throw new SpringException("system.error.property.type.null");
        }
        return applicationContext.getEnvironment().getProperty(key, targetType, defaultValue);
    }
    
    /**
     * 判断是否包含某个 Bean
     *
     * @param name Bean 名称
     * @return 是否包含
     */
    public static boolean containsBean(String name) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (name == null || name.isEmpty()) {
            throw new SpringException("system.error.bean.name.empty");
        }
        return applicationContext.containsBean(name);
    }
    
    /**
     * 判断是否包含某个类型的 Bean
     *
     * @param requiredType Bean 类型
     * @return 是否包含
     */
    public static boolean containsBeanType(Class<?> requiredType) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (requiredType == null) {
            throw new SpringException("system.error.bean.type.null");
        }
        return applicationContext.getBeanNamesForType(requiredType).length > 0;
    }
    
    /**
     * 判断 Bean 是否为单例
     *
     * @param name Bean 名称
     * @return 是否为单例
     */
    public static boolean isSingleton(String name) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (name == null || name.isEmpty()) {
            throw new SpringException("system.error.bean.name.empty");
        }
        return applicationContext.isSingleton(name);
    }
    
    /**
     * 获取 Bean 的类型
     *
     * @param name Bean 名称
     * @return Bean 类型
     */
    public static Class<?> getType(String name) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (name == null || name.isEmpty()) {
            throw new SpringException("system.error.bean.name.empty");
        }
        return applicationContext.getType(name);
    }
    
    /**
     * 获取 Bean 的别名
     *
     * @param name Bean 名称
     * @return Bean 别名数组
     */
    public static String[] getAliases(String name) {
        if (applicationContext == null) {
            throw new SpringException("system.error.context.null");
        }
        if (name == null || name.isEmpty()) {
            throw new SpringException("system.error.bean.name.empty");
        }
        return applicationContext.getAliases(name);
    }
} 