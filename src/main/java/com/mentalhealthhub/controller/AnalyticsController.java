package com.mentalhealthhub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mentalhealthhub.dto.AdminActivityDTO;
import com.mentalhealthhub.dto.AppointmentAnalyticsDTO;
import com.mentalhealthhub.dto.ComprehensiveAnalyticsDTO;
import com.mentalhealthhub.dto.ForumAnalyticsDTO;
import com.mentalhealthhub.dto.MentalHealthTrendsDTO;
import com.mentalhealthhub.dto.ModuleAnalyticsDTO;
import com.mentalhealthhub.dto.ReportAnalyticsDTO;
import com.mentalhealthhub.dto.SelfCareAnalyticsDTO;
import com.mentalhealthhub.dto.StaffDashboardMetricsDTO;
import com.mentalhealthhub.dto.UserAnalyticsDTO;
import com.mentalhealthhub.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

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

    // Staff Dashboard Metrics
    @GetMapping("/staff/dashboard-metrics")
    public ResponseEntity<StaffDashboardMetricsDTO> getStaffDashboardMetrics() {
        StaffDashboardMetricsDTO metrics = analyticsService.getStaffDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }
}