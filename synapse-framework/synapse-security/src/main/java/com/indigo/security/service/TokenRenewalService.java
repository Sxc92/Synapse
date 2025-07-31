package com.indigo.security.service;

import com.indigo.cache.session.UserSessionService;
import com.indigo.core.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Token续期服务
 * 处理Sa-Token的自动续期逻辑
 *
 * @author 史偕成
 * @date 2024/12/19
 */
@Slf4j
@Service
public class TokenRenewalService {

    private final UserSessionService userSessionService;

    // 续期阈值配置（默认30分钟）
    @Value("${synapse.security.token.renewal.threshold:1800}")
    private long renewalThresholdSeconds;
    
    // 续期时间配置（默认2小时）
    @Value("${synapse.security.token.renewal.duration:7200}")
    private long renewalDurationSeconds;

    public TokenRenewalService(@Autowired(required = false) UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    /**
     * 检查并续期token
     * 如果token即将过期（在阈值时间内），则自动续期
     *
     * @param token Sa-Token
     * @return 续期信息
     */
    public TokenRenewalInfo checkAndRenewToken(String token) {
        try {
            // 如果没有UserSessionService，返回有效状态
            if (userSessionService == null) {
                log.debug("UserSessionService不可用，跳过token续期检查");
                return TokenRenewalInfo.valid(token, renewalDurationSeconds);
            }

            // 获取用户会话信息
            UserContext userContext = userSessionService.getUserSession(token);
            if (userContext == null) {
                log.debug("用户会话不存在，token无效: {}", token);
                return TokenRenewalInfo.invalid();
            }

            // 检查token剩余时间
            long remainingSeconds = getTokenRemainingTime(token);
            if (remainingSeconds <= 0) {
                log.debug("Token已过期: {}", token);
                return TokenRenewalInfo.expired();
            }

            // 如果剩余时间小于阈值，则续期
            if (remainingSeconds <= renewalThresholdSeconds) {
                log.info("Token即将过期，开始续期: token={}, remainingSeconds={}", token, remainingSeconds);
                
                // 续期token
                boolean renewed = renewToken(token);
                if (renewed) {
                    log.info("Token续期成功: token={}, newDuration={}", token, renewalDurationSeconds);
                    return TokenRenewalInfo.renewed(token, renewalDurationSeconds);
                } else {
                    log.warn("Token续期失败: token={}", token);
                    return TokenRenewalInfo.renewalFailed();
                }
            }

            // 不需要续期
            return TokenRenewalInfo.valid(token, remainingSeconds);

        } catch (Exception e) {
            log.error("Token续期过程中发生错误: token={}", token, e);
            return TokenRenewalInfo.error();
        }
    }

    /**
     * 强制续期token
     * 无论是否到达阈值，都强制续期
     *
     * @param token Sa-Token
     * @return 是否续期成功
     */
    public boolean forceRenewToken(String token) {
        try {
            boolean renewed = renewToken(token);
            if (renewed) {
                log.info("Token强制续期成功: token={}", token);
            } else {
                log.warn("Token强制续期失败: token={}", token);
            }
            return renewed;
        } catch (Exception e) {
            log.error("Token强制续期过程中发生错误: token={}", token, e);
            return false;
        }
    }

    /**
     * 获取token剩余时间
     *
     * @param token Sa-Token
     * @return 剩余时间（秒），如果token无效返回-1
     */
    public long getTokenRemainingTime(String token) {
        try {
            // 如果没有UserSessionService，返回默认值
            if (userSessionService == null) {
                return renewalDurationSeconds;
            }
            
            return userSessionService.getTokenRemainingTime(token);
        } catch (Exception e) {
            log.error("获取token剩余时间失败: token={}", token, e);
            return -1;
        }
    }

    /**
     * 续期token
     */
    private boolean renewToken(String token) {
        try {
            // 如果没有UserSessionService，返回false
            if (userSessionService == null) {
                log.warn("UserSessionService不可用，无法续期token");
                return false;
            }
            
            return userSessionService.renewToken(token, renewalDurationSeconds);
            
        } catch (Exception e) {
            log.error("续期token失败: token={}", token, e);
            return false;
        }
    }

    /**
     * Token续期信息
     */
    public static class TokenRenewalInfo {
        private final String token;
        private final boolean valid;
        private final boolean renewed;
        private final boolean expired;
        private final long remainingSeconds;
        private final String status;

        private TokenRenewalInfo(String token, boolean valid, boolean renewed, boolean expired, 
                                long remainingSeconds, String status) {
            this.token = token;
            this.valid = valid;
            this.renewed = renewed;
            this.expired = expired;
            this.remainingSeconds = remainingSeconds;
            this.status = status;
        }

        public static TokenRenewalInfo valid(String token, long remainingSeconds) {
            return new TokenRenewalInfo(token, true, false, false, remainingSeconds, "VALID");
        }

        public static TokenRenewalInfo renewed(String token, long newDuration) {
            return new TokenRenewalInfo(token, true, true, false, newDuration, "RENEWED");
        }

        public static TokenRenewalInfo invalid() {
            return new TokenRenewalInfo(null, false, false, false, -1, "INVALID");
        }

        public static TokenRenewalInfo expired() {
            return new TokenRenewalInfo(null, false, false, true, -1, "EXPIRED");
        }

        public static TokenRenewalInfo renewalFailed() {
            return new TokenRenewalInfo(null, false, false, false, -1, "RENEWAL_FAILED");
        }

        public static TokenRenewalInfo error() {
            return new TokenRenewalInfo(null, false, false, false, -1, "ERROR");
        }

        // Getters
        public String getToken() { return token; }
        public boolean isValid() { return valid; }
        public boolean isRenewed() { return renewed; }
        public boolean isExpired() { return expired; }
        public long getRemainingSeconds() { return remainingSeconds; }
        public String getStatus() { return status; }
    }
} 