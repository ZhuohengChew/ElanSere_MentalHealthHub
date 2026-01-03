package com.mentalhealthhub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mentalhealthhub.dto.*;
import com.mentalhealthhub.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Constructor-based dependency injection.
     * Spring IoC container will automatically inject the required dependencies
     * when creating an instance of AnalyticsController.
     * 
     * @param analyticsService Service for analytics-related business logic
     */
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // User Analytics
    @GetMapping("/users")
    public ResponseEntity<UserAnalyticsDTO> getUserAnalytics() {
        UserAnalyticsDTO analytics = analyticsService.getUserAnalytics();
        return ResponseEntity.ok(analytics);
    }

    // Mental Health Trends
    @GetMapping("/mental-health")
    public ResponseEntity<MentalHealthTrendsDTO> getMentalHealthTrends() {
        MentalHealthTrendsDTO analytics = analyticsService.getMentalHealthTrends();
        return ResponseEntity.ok(analytics);
    }

    // Appointment Analytics
    @GetMapping("/appointments")
    public ResponseEntity<AppointmentAnalyticsDTO> getAppointmentAnalytics() {
        AppointmentAnalyticsDTO analytics = analyticsService.getAppointmentAnalytics();
        return ResponseEntity.ok(analytics);
    }

    // Module Analytics
    @GetMapping("/modules")
    public ResponseEntity<ModuleAnalyticsDTO> getModuleAnalytics() {
        ModuleAnalyticsDTO analytics = analyticsService.getModuleAnalytics();
        return ResponseEntity.ok(analytics);
    }

    // Self-Care Analytics
    @GetMapping("/self-care")
    public ResponseEntity<SelfCareAnalyticsDTO> getSelfCareAnalytics() {
        SelfCareAnalyticsDTO analytics = analyticsService.getSelfCareAnalytics();
        return ResponseEntity.ok(analytics);
    }

    // Forum Analytics
    @GetMapping("/forum")
    public ResponseEntity<ForumAnalyticsDTO> getForumAnalytics() {
        ForumAnalyticsDTO analytics = analyticsService.getForumAnalytics();
        return ResponseEntity.ok(analytics);
    }

    // Report Analytics
    @GetMapping("/reports")
    public ResponseEntity<ReportAnalyticsDTO> getReportAnalytics() {
        ReportAnalyticsDTO analytics = analyticsService.getReportAnalytics();
        return ResponseEntity.ok(analytics);
    }

    // Admin Activity
    @GetMapping("/admin-activity")
    public ResponseEntity<AdminActivityDTO> getAdminActivity() {
        AdminActivityDTO analytics = analyticsService.getAdminActivity();
        return ResponseEntity.ok(analytics);
    }

    // Comprehensive Analytics (all data at once)
    @GetMapping("/comprehensive")
    public ResponseEntity<ComprehensiveAnalyticsDTO> getComprehensiveAnalytics() {
        ComprehensiveAnalyticsDTO analytics = analyticsService.getComprehensiveAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
