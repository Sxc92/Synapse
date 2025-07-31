package com.indigo.security.config;

import com.indigo.cache.core.CacheService;
import com.indigo.cache.session.UserSessionService;
import com.indigo.security.core.TokenManager;
import com.indigo.security.core.PermissionManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * 安全自动配置测试
 *
 * @author 史偕成
 * @date 2024/12/19
 */
public class AutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    SecurityAutoConfiguration.class
            ))
            .withUserConfiguration(TestConfiguration.class)
            .withPropertyValues("synapse.security.enabled=true");

    @Test
    public void testSecurityAutoConfiguration() {
        this.contextRunner
                .run(context -> {
                    // 验证核心Bean被正确创建
                    assertThat(context).hasSingleBean(TokenManager.class);
                    assertThat(context).hasSingleBean(PermissionManager.class);
                    assertThat(context).hasSingleBean(UserSessionService.class);
                });
    }

    @Test
    public void testOAuth2ConfigurationDisabled() {
        this.contextRunner
                .withPropertyValues("synapse.oauth2.enabled=false")
                .run(context -> {
                    // OAuth2配置应该被禁用
                    assertThat(context).doesNotHaveBean("oAuth2Config");
                });
    }

    @Test
    public void testSecurityConfigurationDefault() {
        this.contextRunner
                .run(context -> {
                    // 默认情况下安全配置应该启用
                    assertThat(context).hasSingleBean(TokenManager.class);
                    assertThat(context).hasSingleBean(PermissionManager.class);
                    assertThat(context).hasSingleBean(UserSessionService.class);
                });
    }

    /**
     * 测试配置类
     * 提供必要的Mock Bean
     */
    @Configuration
    static class TestConfiguration {

        @Bean
        @Primary
        public CacheService cacheService() {
            return mock(CacheService.class);
        }

        @Bean
        public UserSessionService userSessionService() {
            return mock(UserSessionService.class);
        }
    }
} 