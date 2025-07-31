package com.indigo.security.factory;

import com.indigo.security.strategy.AuthenticationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证策略工厂
 * 管理和提供不同的认证策略实现
 *
 * @author 史偕成
 * @date 2025/01/07
 */
@Slf4j
@Component
public class AuthenticationStrategyFactory {

    private final Map<String, AuthenticationStrategy> strategyMap = new HashMap<>();

    @Autowired
    public AuthenticationStrategyFactory(List<AuthenticationStrategy> strategies) {
        for (AuthenticationStrategy strategy : strategies) {
            strategyMap.put(strategy.getStrategyType(), strategy);
            log.info("注册认证策略: type={}, class={}", 
                strategy.getStrategyType(), strategy.getClass().getSimpleName());
        }
    }

    /**
     * 获取指定类型的认证策略
     *
     * @param strategyType 策略类型
     * @return 认证策略实现
     */
    public AuthenticationStrategy getStrategy(String strategyType) {
        AuthenticationStrategy strategy = strategyMap.get(strategyType);
        if (strategy == null) {
            throw new IllegalArgumentException("未找到认证策略: " + strategyType);
        }
        return strategy;
    }

    /**
     * 获取所有可用的认证策略类型
     *
     * @return 策略类型列表
     */
    public List<String> getAvailableStrategyTypes() {
        return List.copyOf(strategyMap.keySet());
    }

    /**
     * 检查策略类型是否存在
     *
     * @param strategyType 策略类型
     * @return 是否存在
     */
    public boolean hasStrategy(String strategyType) {
        return strategyMap.containsKey(strategyType);
    }
} 