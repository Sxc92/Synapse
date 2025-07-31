//package com.indigo.core.config;
//
//import com.indigo.core.interceptor.SecurityContextInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * Web MVC配置
// *
// * @author 史偕成
// * @date 2024/03/21
// */
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//
//    private final SecurityContextInterceptor securityContextInterceptor;
//
//    public WebMvcConfig(SecurityContextInterceptor securityContextInterceptor) {
//        this.securityContextInterceptor = securityContextInterceptor;
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 注册安全上下文拦截器
//        registry.addInterceptor(securityContextInterceptor)
//                .addPathPatterns("/**")  // 拦截所有请求
//                .excludePathPatterns(    // 排除不需要拦截的路径
//                        "/auth/**",      // 认证相关接口
//                        "/error",        // 错误页面
//                        "/swagger-ui/**",// Swagger UI
//                        "/v3/api-docs/**"// OpenAPI文档
//                );
//    }
//}