package com.mentalhealthhub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * Registers custom interceptors for authorization and session management
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ReportAuthorizationInterceptor reportAuthorizationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register report authorization interceptor
        registry.addInterceptor(reportAuthorizationInterceptor)
                .addPathPatterns("/concerns/**")
                .addPathPatterns("/api/students");
    }
}
