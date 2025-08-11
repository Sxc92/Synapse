package com.indigo.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indigo.cache.session.UserSessionService;
import com.indigo.core.context.UserContext;
import com.indigo.core.entity.Result;
import com.indigo.gateway.config.GatewayConfig;
import com.indigo.security.service.TokenRenewalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Gateway 统一认证过滤器
 * 基于UserSessionService进行权限认证，避免在Gateway中直接使用Sa-Token
 *
 * 架构设计：
 * - 使用UserSessionService进行token验证和权限检查
 * - 使用synapse-cache模块获取用户会话
 * - 专注于token传递、权限认证和限流
 *
 * @author 史偕成
 * @date 2025/01/07
 */
@Slf4j
@Component
public class SaTokenAuthFilter implements GlobalFilter, Ordered {

    private final UserSessionService userSessionService;
    private final TokenRenewalService tokenRenewalService;
    private final ObjectMapper objectMapper;
    private final GatewayConfig gatewayConfig;

    public SaTokenAuthFilter(UserSessionService userSessionService,
                             @Autowired(required = false) TokenRenewalService tokenRenewalService,
                             @Qualifier("synapseObjectMapper") ObjectMapper objectMapper,
                             GatewayConfig gatewayConfig) {
        this.userSessionService = userSessionService;
        this.tokenRenewalService = tokenRenewalService;
        this.objectMapper = objectMapper;
        this.gatewayConfig = gatewayConfig;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        log.debug("Gateway认证过滤器: method={}, path={}", request.getMethod(), path);

        // 检查白名单
        if (isWhiteListed(path)) {
            log.debug("路径在白名单中，跳过认证: path={}", path);
            return chain.filter(exchange);
        }

        // 检查第三方平台认证
        if (isThirdPartyPath(path)) {
            return handleThirdPartyAuth(exchange, chain);
        }

        // 内部用户认证 - 基于UserSessionService
        return handleInternalAuth(exchange, chain);
    }

    /**
     * 处理内部用户认证 - 基于UserSessionService
     */
    private Mono<Void> handleInternalAuth(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractToken(exchange.getRequest());
        if (token == null) {
            log.warn("缺少访问令牌: path={}", exchange.getRequest().getPath().value());
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "缺少访问令牌");
        }

        try {
            // 获取用户上下文 - 通过UserSessionService验证token
            UserContext userContext = userSessionService.getUserSession(token);
            if (userContext == null) {
                log.warn("令牌无效或已过期: token={}, path={}", token, exchange.getRequest().getPath().value());
                return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "令牌无效或已过期");
            }

            // 检查并续期token
            if (gatewayConfig.getTokenRenewal().isEnabled() && tokenRenewalService != null) {
                TokenRenewalService.TokenRenewalInfo renewalInfo = tokenRenewalService.checkAndRenewToken(token);
                if (renewalInfo != null && renewalInfo.isRenewed()) {
                    // 添加续期信息到响应头
                    exchange.getResponse().getHeaders().add("X-Token-Renewed", "true");
                    exchange.getResponse().getHeaders().add("X-Token-Remaining", String.valueOf(renewalInfo.getRemainingSeconds()));
                    log.debug("Token已续期: token={}, remainingSeconds={}", token, renewalInfo.getRemainingSeconds());
                }
            }

            // 获取用户权限和角色
            List<String> roles = userSessionService.getUserRoles(token);
            List<String> permissions = userSessionService.getUserPermissions(token);

            if (roles == null || roles.isEmpty()) {
                log.debug("用户角色列表为空: userId={}, token={}", userContext.getUserId(), token);
            }
            if (permissions == null || permissions.isEmpty()) {
                log.debug("用户权限列表为空: userId={}, token={}", userContext.getUserId(), token);
            }

            // 权限检查
            if (!hasPermissionForPath(exchange.getRequest().getPath().value(), permissions)) {
                log.warn("用户权限不足: userId={}, path={}, permissions={}",
                        userContext.getUserId(), exchange.getRequest().getPath().value(), permissions);
                return writeErrorResponse(exchange, HttpStatus.FORBIDDEN, "权限不足");
            }

            // 将用户信息传递给下游服务
            ServerHttpRequest modifiedRequest = buildAuthenticatedRequest(exchange.getRequest(), userContext, roles, permissions);

            log.debug("用户认证成功: userId={}, username={}, path={}",
                    userContext.getUserId(), userContext.getUsername(), exchange.getRequest().getPath().value());

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            log.error("内部用户认证失败: token={}, path={}", token, exchange.getRequest().getPath().value(), e);
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "认证失败");
        }
    }

    /**
     * 处理第三方平台认证
     */
    private Mono<Void> handleThirdPartyAuth(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!gatewayConfig.getThirdParty().isEnabled()) {
            log.warn("第三方平台认证未启用: path={}", exchange.getRequest().getPath().value());
            return writeErrorResponse(exchange, HttpStatus.FORBIDDEN, "第三方平台认证未启用");
        }

        String apiKey = exchange.getRequest().getHeaders().getFirst(gatewayConfig.getThirdParty().getApiKeyHeader());
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("缺少API Key: path={}", exchange.getRequest().getPath().value());
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "缺少API Key");
        }

        // TODO: 实现第三方平台API Key验证逻辑
        // 这里可以调用专门的第三方平台认证服务

        log.debug("第三方平台认证: apiKey={}, path={}", apiKey, exchange.getRequest().getPath().value());

        // 添加第三方平台标识
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-Auth-Type", "third-party")
                .header("X-API-Key", apiKey)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    /**
     * 构建认证后的请求
     */
    private ServerHttpRequest buildAuthenticatedRequest(ServerHttpRequest request, UserContext userContext,
                                                        List<String> roles, List<String> permissions) {
        ServerHttpRequest.Builder builder = request.mutate()
                .header("X-Auth-Type", "internal")
                .header("X-User-Id", String.valueOf(userContext.getUserId()))
                .header("X-Username", userContext.getUsername());

        // 添加可选字段，确保非空
        if (userContext.getTenantId() != null) {
            builder.header("X-Tenant-Id", String.valueOf(userContext.getTenantId()));
        }
        if (userContext.getDeptId() != null) {
            builder.header("X-Dept-Id", String.valueOf(userContext.getDeptId()));
        }

        // 添加角色和权限信息
        builder.header("X-User-Roles", String.join(",", roles != null ? roles : List.of()))
                .header("X-User-Permissions", String.join(",", permissions != null ? permissions : List.of()));

        // 编码并添加完整的用户上下文
        String encodedContext = encodeUserContext(userContext);
        if (encodedContext != null) {
            builder.header("X-User-Context", encodedContext);
        }

        return builder.build();
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhiteListed(String path) {
        return gatewayConfig.getWhiteList().stream()
                .anyMatch(pattern -> pathMatches(pattern, path));
    }

    /**
     * 检查是否为第三方平台路径
     */
    private boolean isThirdPartyPath(String path) {
        return gatewayConfig.getThirdParty().getPaths().stream()
                .anyMatch(pattern -> pathMatches(pattern, path));
    }

    /**
     * 路径匹配
     */
    private boolean pathMatches(String pattern, String path) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        } else {
            return pattern.equals(path);
        }
    }

    /**
     * 从请求中提取token
     */
    private String extractToken(ServerHttpRequest request) {
        // 从Authorization header获取
        String authorization = request.getHeaders().getFirst("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7).trim();
            if (!token.isEmpty()) {
                return token;
            }
        }

        // 从satoken header获取
        String satoken = request.getHeaders().getFirst("satoken");
        if (satoken != null && !satoken.trim().isEmpty()) {
            return satoken.trim();
        }

        // 从查询参数获取
        String tokenParam = request.getQueryParams().getFirst("token");
        if (tokenParam != null && !tokenParam.trim().isEmpty()) {
            return tokenParam.trim();
        }

        return null;
    }

    /**
     * 权限检查 - 修复后的版本
     */
    private boolean hasPermissionForPath(String path, List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            log.debug("权限检查失败: 权限列表为空, path={}", path);
            return false;
        }

        // 超级管理员拥有所有权限
        if (permissions.contains("admin") || permissions.contains("*")) {
            log.debug("权限检查通过: 超级管理员权限, path={}", path);
            return true;
        }

        // 根据路径映射到所需权限
        String requiredPermission = mapPathToPermission(path);
        if (requiredPermission != null) {
            // 检查用户是否具有所需权限
            boolean hasPermission = permissions.contains(requiredPermission);
            log.debug("权限检查: path={}, requiredPermission={}, hasPermission={}, userPermissions={}",
                    path, requiredPermission, hasPermission, permissions);
            return hasPermission;
        }

        // 默认允许（根据实际需求调整）
        log.debug("权限检查通过: 默认允许, path={}, permissions={}", path, permissions);
        return true;
    }

    /**
     * 将路径映射到权限码
     * 这里可以根据实际业务需求实现更复杂的映射逻辑
     */
    private String mapPathToPermission(String path) {
        // 示例映射逻辑，可以根据实际需求调整
        if (path.startsWith("/api/user")) {
            return "user:read";
        } else if (path.startsWith("/api/admin")) {
            return "admin:access";
        } else if (path.startsWith("/api/system")) {
            return "system:manage";
        }
        return null; // 返回null表示不需要特殊权限检查
    }

    /**
     * 编码用户上下文
     */
    private String encodeUserContext(UserContext userContext) {
        try {
            if (userContext == null) {
                return null;
            }
            return objectMapper.writeValueAsString(userContext);
        } catch (Exception e) {
            log.error("编码用户上下文失败: userId={}", userContext != null ? userContext.getUserId() : null, e);
            return null;
        }
    }

    /**
     * 写入错误响应 - 使用统一的Result格式
     */
    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        // 使用统一的Result格式
        Result<Object> result = Result.error(message);

        try {
            String body = objectMapper.writeValueAsString(result);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
        } catch (Exception e) {
            log.error("写入错误响应失败", e);
            return response.setComplete();
        }
    }

    /**
     * 写入错误响应 - 使用错误码和消息
     */
    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String code, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        // 使用统一的Result格式
        Result<Object> result = Result.error(code, message);

        try {
            String body = objectMapper.writeValueAsString(result);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
        } catch (Exception e) {
            log.error("写入错误响应失败", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}