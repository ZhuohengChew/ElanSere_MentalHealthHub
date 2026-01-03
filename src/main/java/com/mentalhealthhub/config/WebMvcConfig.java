package com.mentalhealthhub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * Registers custom interceptors for authorization and session management
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ReportAuthorizationInterceptor reportAuthorizationInterceptor;

    /**
     * Constructor-based dependency injection.
     * Spring IoC container will automatically inject the required dependencies
     * when creating an instance of WebMvcConfig.
     * 
     * @param reportAuthorizationInterceptor Interceptor for report authorization
     */
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
}
