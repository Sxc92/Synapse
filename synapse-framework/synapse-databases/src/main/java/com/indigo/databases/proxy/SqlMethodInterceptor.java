package com.indigo.databases.proxy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.databases.dto.PageDTO;
import com.indigo.databases.dto.PageResult;
import com.indigo.databases.utils.LambdaQueryBuilder;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.databases.utils.QueryConditionBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * SQL方法拦截器
 * 用于动态代理处理MyBatis-Plus注解SQL方法
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@Component
public class SqlMethodInterceptor implements InvocationHandler {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    // 静态引用，用于在动态代理中访问ApplicationContext
    private static ApplicationContext staticApplicationContext;
    
    @PostConstruct
    public void init() {
        staticApplicationContext = this.applicationContext;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            // 检查是否是Object的方法
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }
            
            // 检查是否是BaseRepository的方法
            if (isBaseRepositoryMethod(method)) {
                return handleBaseRepositoryMethod(proxy, method, args);
            }
            
            // 检查是否是Mapper的方法
            if (isMapperMethod(method)) {
                return callMapperMethod(method, args);
            }
            
            // 如果没有SQL注解，尝试调用默认实现
            if (method.isDefault()) {
                return method.invoke(proxy, args);
            }
            
            // 如果既没有SQL注解也不是默认方法，抛出异常
            throw new UnsupportedOperationException(
                "Method " + method.getName() + " has no default implementation"
            );
        } catch (Exception e) {
            log.error("Error invoking method: {}", method.getName(), e);
            throw e;
        }
    }
    
    /**
     * 调用mapper方法
     */
    private Object callMapperMethod(Method method, Object[] args) throws Exception {
        Object mapper = getMapperInstance(method);
        
        // 尝试在mapper上调用相同的方法
        try {
            Method mapperMethod = mapper.getClass().getMethod(method.getName(), method.getParameterTypes());
            return mapperMethod.invoke(mapper, args);
        } catch (NoSuchMethodException e) {
            // 如果mapper上没有对应方法，抛出异常
            throw new UnsupportedOperationException(
                "Method " + method.getName() + " not found on mapper"
            );
        }
    }
    
    /**
     * 检查是否是BaseRepository的方法
     */
    private boolean isBaseRepositoryMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        // 检查是否是BaseRepository或其父接口的方法
        return BaseRepository.class.isAssignableFrom(declaringClass) || 
               method.getDeclaringClass().getName().contains("IService") ||
               IService.class.isAssignableFrom(declaringClass);
    }
    
    /**
     * 检查是否是Mapper的方法
     */
    private boolean isMapperMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        // 检查是否是BaseMapper或其子接口的方法
        return com.baomidou.mybatisplus.core.mapper.BaseMapper.class.isAssignableFrom(declaringClass) ||
               declaringClass.getName().contains("Mapper");
    }
    
    /**
     * 使用反射创建ServiceImpl实例（避免泛型问题）
     */
    private Object createServiceImplWithReflection(Class<?> entityClass, Class<?> mapperClass, Object mapper) {
        try {
            // 使用反射创建ServiceImpl的匿名子类，避免泛型问题
            Class<?> serviceImplClass = ServiceImpl.class;
            
            // 创建一个简单的对象，只实现必要的方法
            Object serviceImpl = new Object() {
                public BaseMapper<?> getBaseMapper() {
                    return (BaseMapper<?>) mapper;
                }
                
                // 模拟ServiceImpl的save方法
                public boolean save(Object entity) {
                    try {
                        Method insertMethod = mapperClass.getMethod("insert", Object.class);
                        return (Integer) insertMethod.invoke(mapper, entity) > 0;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to save entity", e);
                    }
                }
                
                // 模拟ServiceImpl的getById方法
                public Object getById(Object id) {
                    try {
                        Method selectByIdMethod = mapperClass.getMethod("selectById", Object.class);
                        return selectByIdMethod.invoke(mapper, id);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to get entity by id", e);
                    }
                }
                
                // 模拟ServiceImpl的updateById方法
                public boolean updateById(Object entity) {
                    try {
                        Method updateByIdMethod = mapperClass.getMethod("updateById", Object.class);
                        return (Integer) updateByIdMethod.invoke(mapper, entity) > 0;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to update entity", e);
                    }
                }
                
                // 模拟ServiceImpl的removeById方法
                public boolean removeById(Object id) {
                    try {
                        Method deleteByIdMethod = mapperClass.getMethod("deleteById", Object.class);
                        return (Integer) deleteByIdMethod.invoke(mapper, id) > 0;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to remove entity by id", e);
                    }
                }
                
                // 模拟ServiceImpl的page方法 - 已废弃，现在使用LambdaQueryBuilder
                public com.baomidou.mybatisplus.core.metadata.IPage<?> page(com.baomidou.mybatisplus.extension.plugins.pagination.Page<?> page, com.baomidou.mybatisplus.core.conditions.Wrapper<?> queryWrapper) {
                    // 这个方法不再使用，pageWithCondition直接使用LambdaQueryBuilder
                    throw new UnsupportedOperationException("page method is deprecated, use pageWithCondition instead");
                }
                
                // 模拟ServiceImpl的list方法
                public java.util.List<?> list(com.baomidou.mybatisplus.core.conditions.Wrapper<?> queryWrapper) {
                    try {
                        Method selectListMethod = mapperClass.getMethod("selectList", com.baomidou.mybatisplus.core.conditions.Wrapper.class);
                        return (java.util.List<?>) selectListMethod.invoke(mapper, queryWrapper);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to list query", e);
                    }
                }
                
                // 模拟ServiceImpl的getOne方法
                public Object getOne(com.baomidou.mybatisplus.core.conditions.Wrapper<?> queryWrapper) {
                    try {
                        Method selectOneMethod = mapperClass.getMethod("selectOne", com.baomidou.mybatisplus.core.conditions.Wrapper.class);
                        return selectOneMethod.invoke(mapper, queryWrapper);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to get one", e);
                    }
                }
                
                // 模拟ServiceImpl的count方法
                public long count(com.baomidou.mybatisplus.core.conditions.Wrapper<?> queryWrapper) {
                    try {
                        Method selectCountMethod = mapperClass.getMethod("selectCount", com.baomidou.mybatisplus.core.conditions.Wrapper.class);
                        return (Long) selectCountMethod.invoke(mapper, queryWrapper);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to count", e);
                    }
                }
            };
            
            log.debug("Created ServiceImpl-like instance for entity: {}, mapper: {}", entityClass.getSimpleName(), mapperClass.getSimpleName());
            return serviceImpl;
        } catch (Exception e) {
            log.error("Failed to create ServiceImpl for entity: {}, mapper: {}", entityClass.getSimpleName(), mapperClass.getSimpleName(), e);
            throw new RuntimeException("Failed to create ServiceImpl", e);
        }
    }
    
    /**
     * 在ServiceImpl上查找与Repository方法兼容的方法（同名、参数可赋值）
     */
    private Method findCompatibleMethod(Class<?> serviceImplClass, Method repoMethod, Object[] args) {
        return findCompatibleMethod(serviceImplClass, repoMethod.getName(), args);
    }
    
    /**
     * 在ServiceImpl上查找与指定方法名兼容的方法（同名、参数可赋值）
     */
    private Method findCompatibleMethod(Class<?> serviceImplClass, String methodName, Object[] args) {
        Method[] methods = serviceImplClass.getMethods();
        
        outer:
        for (Method m : methods) {
            if (!m.getName().equals(methodName)) continue;
            
            Class<?>[] paramTypes = m.getParameterTypes();
            if (paramTypes.length != (args == null ? 0 : args.length)) continue;
            
            for (int i = 0; i < paramTypes.length; i++) {
                if (args[i] != null && !paramTypes[i].isAssignableFrom(args[i].getClass())) {
                    continue outer;
                }
            }
            return m;
        }
        return null;
    }
    
    /**
     * 获取Mapper实例
     */
    private Object getMapperInstance(Method method) throws Exception {
        // 获取Repository接口的泛型参数
        Class<?> repositoryInterface = method.getDeclaringClass();
        Type[] genericInterfaces = repositoryInterface.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;
                if (pt.getRawType().getTypeName().equals(BaseRepository.class.getName())) {
                    Class<?> mapperClass = (Class<?>) pt.getActualTypeArguments()[1];
                    
                    if (staticApplicationContext == null) {
                        throw new RuntimeException("ApplicationContext not initialized");
                    }
                    return staticApplicationContext.getBean(mapperClass);
                }
            }
        }
        throw new RuntimeException("Cannot resolve Mapper for method: " + method.getName());
    }
    
    /**
     * 处理BaseRepository的方法
     */
    private Object handleBaseRepositoryMethod(Object proxy, Method method, Object[] args) throws Exception {
        log.debug("Handling BaseRepository method: {}", method.getName());
        
        // 特殊处理getBaseMapper方法
        if ("getBaseMapper".equals(method.getName())) {
            return handleGetBaseMapper(proxy);
        }
        
        // 处理default方法
        if (method.isDefault()) {
            log.info("Calling default method: {} with args: {}", method.getName(), args);
            // 对于default方法，我们需要直接执行其实现逻辑，而不是通过代理调用
            return executeDefaultMethod(proxy, method, args);
        }
        
        // 1. 获取Repository接口的泛型参数
        Class<?> repositoryInterface = proxy.getClass().getInterfaces()[0];
        Type[] genericInterfaces = repositoryInterface.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;
                if (pt.getRawType().getTypeName().equals(BaseRepository.class.getName())) {
                    Class<?> entityClass = (Class<?>) pt.getActualTypeArguments()[0];
                    Class<?> mapperClass = (Class<?>) pt.getActualTypeArguments()[1];
                    
                    // 2. 获取Mapper实例
                    if (staticApplicationContext == null) {
                        throw new RuntimeException("ApplicationContext not initialized. Please check if SqlMethodInterceptor is properly configured.");
                    }
                    Object mapper = staticApplicationContext.getBean(mapperClass);
                    
                    // 3. 使用反射创建ServiceImpl实例（避免泛型问题）
                    Object serviceImpl = createServiceImplWithReflection(entityClass, mapperClass, mapper);
                    
                    // 4. 调用ServiceImpl的方法
                    Method serviceMethod = findCompatibleMethod(serviceImpl.getClass(), method, args);
                    if (serviceMethod != null) {
                        log.info("Calling ServiceImpl method: {} with args: {}", method.getName(), args);
                        return serviceMethod.invoke(serviceImpl, args);
                    }
                    
                    throw new UnsupportedOperationException("No compatible method found in ServiceImpl for: " + method.getName());
                }
            }
        }
        
        throw new UnsupportedOperationException("Cannot resolve ServiceImpl for BaseRepository method: " + method.getName());
    }
    
    /**
     * 执行default方法的实现逻辑
     */
    private Object executeDefaultMethod(Object proxy, Method method, Object[] args) throws Exception {
        String methodName = method.getName();
        
        // 获取Repository接口的泛型参数
        Class<?> repositoryInterface = proxy.getClass().getInterfaces()[0];
        Type[] genericInterfaces = repositoryInterface.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;
                if (pt.getRawType().getTypeName().equals(BaseRepository.class.getName())) {
                    Class<?> entityClass = (Class<?>) pt.getActualTypeArguments()[0];
                    Class<?> mapperClass = (Class<?>) pt.getActualTypeArguments()[1];
                    
                    // 获取Mapper实例
                    if (staticApplicationContext == null) {
                        throw new RuntimeException("ApplicationContext not initialized");
                    }
                    Object mapper = staticApplicationContext.getBean(mapperClass);
                    
                    // 根据方法名执行相应的逻辑
                    switch (methodName) {
                        case "pageWithCondition":
                            if (args.length == 1 && args[0] instanceof PageDTO) {
                                PageDTO queryDTO = (PageDTO) args[0];
                                
                                // 直接使用LambdaQueryBuilder，避免调用IService的page方法
                                return LambdaQueryBuilder.pageWithCondition((BaseMapper<?>) mapper, queryDTO);
                            }
                            break;
                        case "listWithCondition":
                            if (args.length == 1) {
                                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<?> wrapper = 
                                    com.indigo.databases.utils.QueryConditionBuilder.buildQueryWrapper(args[0]);
                                return callIServiceMethod(proxy, "list", new Object[]{wrapper});
                            }
                            break;
                        case "getOneWithCondition":
                            if (args.length == 1) {
                                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<?> wrapper = 
                                    com.indigo.databases.utils.QueryConditionBuilder.buildQueryWrapper(args[0]);
                                return callIServiceMethod(proxy, "getOne", new Object[]{wrapper});
                            }
                            break;
                        case "countWithCondition":
                            if (args.length == 1) {
                                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<?> wrapper = 
                                    com.indigo.databases.utils.QueryConditionBuilder.buildQueryWrapper(args[0]);
                                return callIServiceMethod(proxy, "count", new Object[]{wrapper});
                            }
                            break;
                    }
                }
            }
        }
        
        throw new UnsupportedOperationException("Default method not implemented: " + methodName);
    }
    
    /**
     * 调用IService方法
     */
    private Object callIServiceMethod(Object proxy, String methodName, Object[] args) throws Exception {
        // 获取Repository接口的泛型参数
        Class<?> repositoryInterface = proxy.getClass().getInterfaces()[0];
        Type[] genericInterfaces = repositoryInterface.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;
                if (pt.getRawType().getTypeName().equals(BaseRepository.class.getName())) {
                    Class<?> entityClass = (Class<?>) pt.getActualTypeArguments()[0];
                    Class<?> mapperClass = (Class<?>) pt.getActualTypeArguments()[1];
                    
                    // 获取Mapper实例
                    if (staticApplicationContext == null) {
                        throw new RuntimeException("ApplicationContext not initialized");
                    }
                    Object mapper = staticApplicationContext.getBean(mapperClass);
                    
                    // 创建ServiceImpl实例
                    Object serviceImpl = createServiceImplWithReflection(entityClass, mapperClass, mapper);
                    
                    // 调用IService方法
                    Method serviceMethod = findCompatibleMethod(serviceImpl.getClass(), methodName, args);
                    if (serviceMethod != null) {
                        return serviceMethod.invoke(serviceImpl, args);
                    }
                }
            }
        }
        
        throw new UnsupportedOperationException("IService method not found: " + methodName);
    }

    /**
     * 处理getBaseMapper方法
     */
    private Object handleGetBaseMapper(Object proxy) throws Exception {
        // 获取Repository接口的泛型参数
        Class<?> repositoryInterface = proxy.getClass().getInterfaces()[0];
        Type[] genericInterfaces = repositoryInterface.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;
                if (pt.getRawType().getTypeName().equals(BaseRepository.class.getName())) {
                    Class<?> mapperClass = (Class<?>) pt.getActualTypeArguments()[1];
                    
                    if (staticApplicationContext == null) {
                        throw new RuntimeException("ApplicationContext not initialized");
                    }
                    return staticApplicationContext.getBean(mapperClass);
                }
            }
        }
        throw new RuntimeException("Cannot resolve Mapper for getBaseMapper method");
    }
    

} 