package com.indigo.security.core;

import cn.dev33.satoken.stp.StpLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JWT认证逻辑
 * 基于Sa-Token框架，生成JWT token用于微服务间调用
 * 用户信息存储在Redis中
 * 
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@Component
public class JWTStpLogic extends StpLogic {

    public JWTStpLogic() {
        super("jwt");  // 使用jwt作为登录类型
        log.info("JWT认证逻辑初始化完成 - 基于Sa-Token框架，支持微服务间JWT调用");
    }

    @Override
    public String getTokenValue() {
        // 可以在这里自定义JWT token的获取逻辑
        return super.getTokenValue();
    }

    @Override
    public void setTokenValue(String tokenValue) {
        // 可以在这里自定义JWT token的设置逻辑
        super.setTokenValue(tokenValue);
    }
} 