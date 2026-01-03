package com.mentalhealthhub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * Registers custom interceptors for authorization and session management
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ReportAuthorizationInterceptor reportAuthorizationInterceptor;

    public WebMvcConfig(ReportAuthorizationInterceptor reportAuthorizationInterceptor) {
        this.reportAuthorizationInterceptor = reportAuthorizationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register report authorization interceptor
        registry.addInterceptor(reportAuthorizationInterceptor)
                .addPathPatterns("/concerns/**")
                .addPathPatterns("/api/students");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Explicitly map root path to redirect to login
        registry.addViewController("/").setViewName("redirect:/login");
    }
}
