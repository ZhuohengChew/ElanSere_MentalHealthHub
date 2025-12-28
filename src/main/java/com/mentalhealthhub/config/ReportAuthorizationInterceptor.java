package com.mentalhealthhub.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Authorization Interceptor for Report/Concern endpoints
 * Ensures proper session validation and role-based access control
 */
@Component
public class ReportAuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String requestURI = request.getRequestURI();
        
        // Apply authorization checks only to /concerns endpoints
        if (!requestURI.contains("/concerns")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        User user = null;

        // Check if user session exists
        if (session != null) {
            user = (User) session.getAttribute("user");
        }

        // Redirect to login if not authenticated
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // Check authorization based on endpoint and user role
        return checkEndpointAuthorization(requestURI, user, response);
    }

    /**
     * Validates endpoint access based on user role and endpoint path
     */
    private boolean checkEndpointAuthorization(String uri, User user, HttpServletResponse response) 
            throws Exception {
        
        UserRole userRole = user.getRole();

        // Public concerns endpoints (accessible by all authenticated users)
        if (uri.matches(".*/concerns$") || uri.matches(".*/concerns/list$") || uri.matches(".*/concerns/\\d+$")) {
            // All authenticated users can access
            return true;
        }

        // Submit concern endpoint (students can submit their own)
        if (uri.matches(".*/concerns/submit$")) {
            if (userRole == UserRole.STUDENT) {
                return true;
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // API endpoints for getting personal reports
        if (uri.matches(".*/concerns/api/my-reports$") || 
            uri.matches(".*/concerns/api/my-statistics$") ||
            uri.matches(".*/concerns/api/.*") && uri.contains("my-")) {
            // All authenticated users can get their own data
            return true;
        }

        // Staff-only endpoints
        if (uri.matches(".*/concerns/staff-report$") || uri.matches(".*/concerns/staff-submit$")) {
            if (userRole == UserRole.STAFF) {
                return true;
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // Professional/Admin endpoints for report management
        if (uri.matches(".*/concerns/api/.*") && 
            (uri.contains("status") || uri.contains("resolve") || uri.contains("open-reports") || 
             uri.contains("by-status") || uri.contains("by-urgency") || uri.contains("reports-by-date"))) {
            
            if (userRole == UserRole.STAFF || userRole == UserRole.PROFESSIONAL || userRole == UserRole.ADMIN) {
                return true;
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // Student endpoints
        if (uri.matches(".*/concerns/api/students$")) {
            if (userRole == UserRole.STAFF || userRole == UserRole.PROFESSIONAL || userRole == UserRole.ADMIN) {
                return true;
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // Default allow
        return true;
    }
}
