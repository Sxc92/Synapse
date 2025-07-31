package com.indigo.databases.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

/**
 * 代理工厂Bean
 * 用于创建动态代理实例
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
public class ProxyFactoryBean implements FactoryBean<Object> {
    
    private Class<?> targetClass;
    private Object proxyObject;
    
    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }
    
    public void setProxyObject(Object proxyObject) {
        this.proxyObject = proxyObject;
    }
    
    @Override
    public Object getObject() throws Exception {
        if (proxyObject != null) {
            return proxyObject;
        }
        
        if (targetClass == null) {
            throw new IllegalStateException("Target class not set");
        }
        
        return proxyObject;
    }
    
    @Override
    public Class<?> getObjectType() {
        return targetClass;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
} 