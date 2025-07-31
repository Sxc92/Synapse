package com.indigo.security.core;

import cn.dev33.satoken.stp.StpUtil;
import com.indigo.cache.session.UserSessionService;
import com.indigo.core.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 简化的Token管理服务
 * 基于Sa-Token提供基础的令牌管理功能
 *
 * @author 史偕成
 * @date 2024/01/08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenManager {

    private final UserSessionService userSessionService;
//    private final SessionManager sessionManager;

    /**
     * 执行用户登录
     *
     * @param userId      用户ID
     * @param userContext 用户上下文信息
     * @return Sa-Token
     */
    public String login(Object userId, UserContext userContext) {
        if (userId == null) {
            log.error("用户登录失败: userId为空");
            throw new IllegalArgumentException("用户ID不能为空");
        }

        try {
            log.info("开始用户登录流程: userId={}", userId);

            // 执行Sa-Token登录
            StpUtil.login(userId);
            String token = StpUtil.getTokenValue();

            if (token == null || token.trim().isEmpty()) {
                log.error("Sa-Token登录失败: 未能生成有效token");
                throw new RuntimeException("Token生成失败");
            }

            // 获取token超时时间
            long tokenTimeout = StpUtil.getTokenTimeout();
            if (tokenTimeout <= 0) {
                log.warn("Token超时时间异常，使用默认值7200秒: userId={}", userId);
                tokenTimeout = 7200;
            }

            // 存储用户信息到Redis
            if (userContext != null) {
                try {
                    userSessionService.storeUserSession(token, userContext, tokenTimeout);
                    userSessionService.storeUserPermissions(token, userContext.getPermissions(), tokenTimeout);
                    log.info("用户登录成功: userId={}, token={}, timeout={}", userId, token, tokenTimeout);
                } catch (Exception e) {
                    log.error("存储用户会话失败，执行登出操作: userId={}, token={}", userId, token, e);
                    try {
                        StpUtil.logout(userId);
                    } catch (Exception logoutEx) {
                        log.error("登出操作失败", logoutEx);
                    }
                    throw new RuntimeException("存储用户会话失败", e);
                }
            } else {
                log.warn("用户登录成功但无上下文信息: userId={}, token={}", userId, token);
            }

            return token;

        } catch (Exception e) {
            log.error("用户登录失败: userId={}", userId, e);
            throw new RuntimeException("登录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证Token
     *
     * @param token Token值
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.debug("Token验证失败: token为空");
            return false;
        }

        try {
            // 验证token是否存在且未过期
            Object loginId = StpUtil.stpLogic.getLoginIdByToken(token);
            if (loginId == null) {
                log.debug("Token验证失败: 无法获取loginId, token={}", token);
                return false;
            }

            // 验证用户会话是否存在
            UserContext userContext = userSessionService.getUserSession(token);
            if (userContext == null) {
                log.debug("Token验证失败: 用户会话不存在, token={}", token);
                return false;
            }

            log.debug("Token验证成功: token={}, userId={}", token, loginId);
            return true;

        } catch (Exception e) {
            log.debug("Token验证异常: token={}, error={}", token, e.getMessage());
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token Token值
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.debug("获取用户ID失败: token为空");
            return null;
        }

        try {
            Object loginId = StpUtil.stpLogic.getLoginIdByToken(token);
            if (loginId != null) {
                log.debug("获取用户ID成功: token={}, userId={}", token, loginId);
                return loginId.toString();
            } else {
                log.debug("获取用户ID失败: token无效, token={}", token);
                return null;
            }
        } catch (Exception e) {
            log.debug("获取用户ID异常: token={}", token, e);
            return null;
        }
    }

    /**
     * 撤销Token
     *
     * @param token Token值
     */
    public void revokeToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.debug("撤销Token失败: token为空");
            return;
        }

        try {
            String userId = getUserIdFromToken(token);
            if (userId != null) {
                // 先清除Redis中的会话信息
                userSessionService.removeUserSession(token);
                // 然后执行Sa-Token登出
                StpUtil.logout(userId);
                log.info("Token撤销成功: userId={}, token={}", userId, token);
            } else {
                log.warn("Token撤销失败: 无法获取用户信息, token={}", token);
            }
        } catch (Exception e) {
            log.error("撤销Token异常: token={}", token, e);
        }
    }

    /**
     * 检查用户是否已登录
     *
     * @return 是否已登录
     */
    public boolean isLogin() {
        try {
            boolean isLogin = StpUtil.isLogin();
            if (isLogin) {
                String token = StpUtil.getTokenValue();
                Object loginId = StpUtil.getLoginId();
                log.debug("用户已登录: userId={}, token={}", loginId, token);
            }
            return isLogin;
        } catch (Exception e) {
            log.debug("检查登录状态异常", e);
            return false;
        }
    }

    /**
     * 获取当前登录的用户ID
     *
     * @return 用户ID
     */
    public Object getCurrentUserId() {
        try {
            Object loginId = StpUtil.getLoginId();
            if (loginId != null) {
                log.debug("获取当前用户ID成功: userId={}", loginId);
            }
            return loginId;
        } catch (Exception e) {
            log.debug("获取当前用户ID异常", e);
            return null;
        }
    }

    /**
     * 获取当前Token
     *
     * @return Token值
     */
    public String getCurrentToken() {
        try {
            String token = StpUtil.getTokenValue();
            if (token != null) {
                log.debug("获取当前Token成功: token={}", token);
            }
            return token;
        } catch (Exception e) {
            log.debug("获取当前Token异常", e);
            return null;
        }
    }
} 