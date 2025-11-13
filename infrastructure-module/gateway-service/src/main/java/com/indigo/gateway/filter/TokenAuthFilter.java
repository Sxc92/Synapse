package com.indigo.gateway.filter;

import com.indigo.cache.session.UserSessionService;
import com.indigo.core.context.UserContext;
import com.indigo.core.entity.Result;
import com.indigo.core.utils.JsonUtils;
import com.indigo.security.config.SecurityProperties;
import com.indigo.security.constants.SecurityConstants;
import com.indigo.security.constants.SecurityError;
import com.indigo.security.utils.GatewaySignatureUtils;
import com.indigo.security.utils.TokenConfigHelper;
import com.indigo.security.utils.UserContextCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Token 认证过滤器
 * 从请求中提取 token，验证 token 有效性，并将 token 传递给下游服务
 * 
 * 核心逻辑：
 * 1. 检查请求路径是否在白名单中，如果是则直接放行
 * 2. 从请求头或查询参数中提取 token
 * 3. 使用 UserSessionService 验证 token 有效性
 * 4. 将 token 添加到 Authorization 请求头，传递给下游服务
 * 5. 下游服务通过 UserContextInterceptor 从 token 获取用户信息
 * 
 * @author 史偕成
 * @date 2025/01/10
 */
@Slf4j
@Component
public class TokenAuthFilter implements GlobalFilter, Ordered {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final UserSessionService userSessionService;
    private final SecurityProperties securityProperties;

    /**
     * 构造函数：验证 Bean 是否被创建
     * 
     * @param userSessionService 用户会话服务（必须）
     * @param securityProperties 安全配置（通过 GatewayConfig 确保可用）
     */
    public TokenAuthFilter(UserSessionService userSessionService, 
                          SecurityProperties securityProperties) {
        this.userSessionService = userSessionService;
        this.securityProperties = securityProperties;
        log.debug("========== [TokenAuthFilter] Bean 已创建，UserSessionService: {}, SecurityProperties: {} ==========",
                userSessionService != null ? "已注入" : "未注入",
                securityProperties != null ? "已注入" : "未注入");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        log.debug("========== [TokenAuthFilter] 过滤器执行: path={} ==========", path);
        
        // 检查 UserSessionService 是否可用
        if (userSessionService == null) {
            log.warn("UserSessionService 未注入，跳过 Token 认证: path={}", path);
            return chain.filter(exchange);
        }

        // 1. 检查白名单
        if (isWhiteListPath(path)) {
            log.info("白名单路径，跳过认证: {}", path);
            return chain.filter(exchange);
        }

        // 2. 提取 token（在 WebFlux 环境中，需要从请求头中提取）
        String token = extractToken(request);
        log.info("从请求中提取的 token: {}", token != null ? "存在" : "不存在");
        if (!StringUtils.hasText(token)) {
            log.warn("请求中未找到 token: path={}", path);
            return unauthorized(exchange, SecurityError.TOKEN_MISSING);
        }

        // 3. 验证 token 并获取用户信息
        try {
            // 验证 token 是否存在
            String userId = userSessionService.validateToken(token);
            if (userId == null) {
                log.warn("Token 无效或已过期: token={}, path={}", token, path);
                return unauthorized(exchange, SecurityError.TOKEN_INVALID);
            }

            // 获取用户上下文
            UserContext userContext = userSessionService.getUserSession(token);
            if (userContext == null) {
                log.warn("无法获取用户会话信息: token={}, userId={}, path={}", token, userId, path);
                return unauthorized(exchange, SecurityError.SESSION_INVALID);
            }

            // 4. 将用户信息存储到 exchange attributes 中（供后续过滤器使用）
            exchange.getAttributes().put(SecurityConstants.EXCHANGE_ATTR_TOKEN, token);
            exchange.getAttributes().put(SecurityConstants.EXCHANGE_ATTR_USER_CONTEXT, userContext);

            // 5. 构建转发请求，注入用户上下文和签名
            ServerHttpRequest.Builder requestBuilder = request.mutate();
            
            // 5.1 确保 token 在 Authorization 请求头中
            String tokenHeaderName = TokenConfigHelper.getTokenHeaderName(securityProperties);
            String tokenPrefix = TokenConfigHelper.getTokenPrefix(securityProperties);
            String authHeader = request.getHeaders().getFirst(tokenHeaderName);
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(tokenPrefix)) {
                requestBuilder.header(tokenHeaderName, tokenPrefix + token);
            }
            
            // 5.2 注入用户上下文和权限（如果启用）
            SecurityProperties.GatewaySignatureConfig signatureConfig = 
                    securityProperties != null ? securityProperties.getGatewaySignature() : null;
            
            if (signatureConfig != null && signatureConfig.isEnableContextPassing()) {
                // 编码用户上下文
                String encodedContext = UserContextCodec.encode(userContext);
                if (encodedContext != null) {
                    requestBuilder.header(SecurityConstants.X_USER_CONTEXT_HEADER, encodedContext);
                    log.debug("已注入用户上下文到请求头: userId={}", userContext.getUserId());
                }
                
                // 编码权限列表（如果有）
                List<String> permissions = userSessionService.getUserPermissions(token);
                if (permissions != null && !permissions.isEmpty()) {
                    String encodedPermissions = String.join(",", permissions);
                    requestBuilder.header(SecurityConstants.X_USER_PERMISSIONS_HEADER, encodedPermissions);
                    log.debug("已注入用户权限到请求头: permissions={}", permissions);
                }
                
                // 5.3 生成并注入签名（如果启用签名验证）
                if (signatureConfig.isEnabled() && StringUtils.hasText(signatureConfig.getSecret())) {
                    long timestamp = System.currentTimeMillis();
                    String signature = GatewaySignatureUtils.generateSignature(
                            signatureConfig.getSecret(),
                            token,
                            userContext.getUserId(),
                            timestamp
                    );
                    
                    if (signature != null) {
                        requestBuilder.header(SecurityConstants.X_GATEWAY_SIGNATURE_HEADER, signature);
                        requestBuilder.header(SecurityConstants.X_GATEWAY_TIMESTAMP_HEADER, String.valueOf(timestamp));
                        log.debug("已注入 Gateway 签名: userId={}, timestamp={}", 
                                userContext.getUserId(), timestamp);
                    } else {
                        log.warn("生成 Gateway 签名失败: userId={}", userContext.getUserId());
                    }
                }
            }
            
            ServerHttpRequest modifiedRequest = requestBuilder.build();

            log.debug("Token 认证成功: userId={}, account={}, path={}", 
                    userContext.getUserId(), userContext.getAccount(), path);

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            log.error("Token 认证异常: token={}, path={}", token, path, e);
            return unauthorized(exchange, SecurityError.SECURITY_ERROR);
        }
    }

    /**
     * 检查路径是否在白名单中
     * 从 SecurityProperties 获取白名单配置
     */
    private boolean isWhiteListPath(String path) {
        if (securityProperties == null || securityProperties.getWhiteList() == null) {
            return false;
        }

        SecurityProperties.WhiteListConfig whiteListConfig = securityProperties.getWhiteList();
        if (!whiteListConfig.isEnabled()) {
            return false;
        }

        List<String> whiteList = whiteListConfig.getAllPaths();
        if (whiteList == null || whiteList.isEmpty()) {
            return false;
        }

        boolean matched = whiteList.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
        if (matched) {
            log.debug("路径匹配白名单: path={}", path);
        }
        return matched;
    }

    /**
     * 从请求中提取 token
     * 优先级：Authorization Bearer > X-Auth-Token > 查询参数 token
     */
    private String extractToken(ServerHttpRequest request) {
        // 获取配置值
        String tokenHeaderName = TokenConfigHelper.getTokenHeaderName(securityProperties);
        String tokenPrefix = TokenConfigHelper.getTokenPrefix(securityProperties);
        int prefixLength = TokenConfigHelper.getTokenPrefixLength(securityProperties);
        String xAuthTokenHeader = TokenConfigHelper.getXAuthTokenHeader(securityProperties);
        String tokenQueryParam = TokenConfigHelper.getTokenQueryParam(securityProperties);

        // 1. 从 Authorization 头获取
        String authHeader = request.getHeaders().getFirst(tokenHeaderName);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(tokenPrefix)) {
            return authHeader.substring(prefixLength).trim();
        }

        // 2. 从 X-Auth-Token 头获取
        String token = request.getHeaders().getFirst(xAuthTokenHeader);
        if (StringUtils.hasText(token)) {
            return token.trim();
        }

        // 3. 从查询参数获取
        token = request.getQueryParams().getFirst(tokenQueryParam);
        if (StringUtils.hasText(token)) {
            return token.trim();
        }

        return null;
    }

    /**
     * 返回未授权响应
     * 使用 Result 统一返回格式和标准错误码
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, SecurityError errorCode) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        // 使用 Result 统一返回格式
        Result<Void> result = Result.error(errorCode);
        String body = JsonUtils.toJsonString(result);
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        // 在 LoggingFilter 之后执行
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}

