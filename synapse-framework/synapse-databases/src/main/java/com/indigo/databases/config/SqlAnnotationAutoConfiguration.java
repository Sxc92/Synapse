package com.indigo.databases.config;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.AutoService;
import com.indigo.databases.proxy.SqlMethodInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * SQL注解自动配置类
 * 自动扫描并注册带有@AutoRepository和@AutoService注解的接口
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@AutoConfiguration
public class SqlAnnotationAutoConfiguration {
    
    @Autowired
    private SqlMethodInterceptor sqlMethodInterceptor;
    
    
    /**
     * 自动注册Repository代理
     */
    @Bean
    public RepositoryProxyRegistrar repositoryProxyRegistrar() {
        return new RepositoryProxyRegistrar();
    }
    
    /**
     * 自动注册Service代理
     */
    @Bean
    public ServiceProxyRegistrar serviceProxyRegistrar() {
        return new ServiceProxyRegistrar();
    }
    
    /**
     * 自动注册Repository代理
     */
    @Bean
    public AutoRepositoryRegistrar autoRepositoryRegistrar() {
        return new AutoRepositoryRegistrar();
    }
    
    /**
     * 自动Repository注册器
     */
    public static class AutoRepositoryRegistrar implements BeanFactoryPostProcessor {
        
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            try {
                log.info("开始扫描@AutoRepository注解的接口...");
                
                // 扫描所有带有@AutoRepository注解的接口
                Reflections reflections = new Reflections("com.indigo");
                Set<Class<?>> repositoryInterfaces = reflections.getTypesAnnotatedWith(AutoRepository.class);
                
                log.info("找到{}个带有@AutoRepository注解的接口", repositoryInterfaces.size());
                
                for (Class<?> repositoryInterface : repositoryInterfaces) {
                    log.info("发现Repository接口: {}", repositoryInterface.getName());
                    if (repositoryInterface.isInterface()) {
                        registerRepositoryProxy(repositoryInterface, beanFactory);
                    }
                }
            } catch (Exception e) {
                log.error("Error registering auto repositories", e);
            }
        }
        
        /**
         * 注册单个Repository代理
         */
        private void registerRepositoryProxy(Class<?> repositoryInterface, ConfigurableListableBeanFactory beanFactory) {
            try {
                String beanName = repositoryInterface.getSimpleName();
                
                // 创建代理对象
                Object proxy = Proxy.newProxyInstance(
                    repositoryInterface.getClassLoader(),
                    new Class<?>[]{repositoryInterface},
                    new SqlMethodInterceptor()
                );
                
                // 注册为Spring Bean
                if (!beanFactory.containsBean(beanName)) {
                    if (beanFactory instanceof DefaultListableBeanFactory) {
                        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
                        
                        // 使用FactoryBean来创建代理
                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProxyFactoryBean.class);
                        builder.addPropertyValue("targetClass", repositoryInterface);
                        builder.addPropertyValue("proxyObject", proxy);
                        
                        BeanDefinition beanDefinition = builder.getBeanDefinition();
                        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
                        
                        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinition);
                        log.info("Registered AutoRepository proxy: {} as {}", beanName, repositoryInterface.getName());
                    }
                } else {
                    log.info("Bean {} already exists, skipping registration", beanName);
                }
            } catch (Exception e) {
                log.error("Error registering repository proxy for {}", repositoryInterface.getName(), e);
            }
        }
    }
    
    /**
     * Repository代理注册器
     */
    @Component
    public static class RepositoryProxyRegistrar implements BeanFactoryPostProcessor {
        
        @Autowired
        private SqlMethodInterceptor sqlMethodInterceptor;
        
        @Override
        public void postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory beanFactory) throws BeansException {
            registerProxies((BeanDefinitionRegistry) beanFactory);
        }
        
        public void registerProxies() {
            // 这个方法保留用于兼容性
        }
        
        public void registerProxies(BeanDefinitionRegistry registry) {
            try {
                // 扫描带有@AutoRepository注解的接口
                ClassPathScanningCandidateComponentProvider scanner = 
                    new ClassPathScanningCandidateComponentProvider(false);
                scanner.addIncludeFilter(new AnnotationTypeFilter(AutoRepository.class));
                
                Set<BeanDefinition> candidates = scanner.findCandidateComponents("com.indigo");
                
                for (BeanDefinition beanDefinition : candidates) {
                    String className = beanDefinition.getBeanClassName();
                    Class<?> interfaceClass = Class.forName(className);
                    
                    if (interfaceClass.isInterface()) {
                        // 创建BeanDefinition
                        GenericBeanDefinition beanDef = new GenericBeanDefinition();
                        beanDef.setBeanClass(ProxyFactoryBean.class);
                        beanDef.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                        
                        // 设置代理接口
                        beanDef.getPropertyValues().add("proxyInterface", interfaceClass);
                        
                        // 注册为Spring Bean
                        String beanName = interfaceClass.getSimpleName();
                        if (!registry.containsBeanDefinition(beanName)) {
                            registry.registerBeanDefinition(beanName, beanDef);
                            log.info("Registered Repository proxy: {}", beanName);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error registering Repository proxies", e);
            }
        }
    }
    
    /**
     * Service代理注册器
     */
    @Component
    public static class ServiceProxyRegistrar implements BeanFactoryPostProcessor {
        
        @Autowired
        private SqlMethodInterceptor sqlMethodInterceptor;
        
        @Override
        public void postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory beanFactory) throws BeansException {
            registerProxies((BeanDefinitionRegistry) beanFactory);
        }
        
        public void registerProxies() {
            // 这个方法保留用于兼容性
        }
        
        public void registerProxies(BeanDefinitionRegistry registry) {
            try {
                // 扫描带有@AutoService注解的接口
                ClassPathScanningCandidateComponentProvider scanner = 
                    new ClassPathScanningCandidateComponentProvider(false);
                scanner.addIncludeFilter(new AnnotationTypeFilter(AutoService.class));
                
                Set<BeanDefinition> candidates = scanner.findCandidateComponents("com.indigo");
                
                for (BeanDefinition beanDefinition : candidates) {
                    String className = beanDefinition.getBeanClassName();
                    Class<?> interfaceClass = Class.forName(className);
                    
                    if (interfaceClass.isInterface()) {
                        // 创建BeanDefinition
                        GenericBeanDefinition beanDef = new GenericBeanDefinition();
                        beanDef.setBeanClass(ProxyFactoryBean.class);
                        beanDef.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                        
                        // 设置代理接口
                        beanDef.getPropertyValues().add("proxyInterface", interfaceClass);
                        
                        // 注册为Spring Bean
                        String beanName = interfaceClass.getSimpleName();
                        if (!registry.containsBeanDefinition(beanName)) {
                            registry.registerBeanDefinition(beanName, beanDef);
                            log.info("Registered Service proxy: {}", beanName);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error registering Service proxies", e);
            }
        }
    }
} 