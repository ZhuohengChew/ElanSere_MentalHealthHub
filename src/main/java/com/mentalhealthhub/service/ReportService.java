package com.mentalhealthhub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mentalhealthhub.model.Report;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.ReportRepository;

@Service
@Transactional
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    // ==================== CREATE Operations ====================
    /**
     * Create a new report submitted by a student
     */
    public Report createReport(User student, String type, String description, String urgency) {
        Report report = new Report();
        report.setStudent(student);
        report.setType(type);
        report.setDescription(description);
        report.setUrgency(urgency);
        report.setStatus("pending");
        report.setSubmittedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    // ==================== READ Operations ====================
    /**
     * Get report by ID
     */
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    /**
     * Get all reports for a student
     */
    public List<Report> getStudentReports(Long studentId) {
        return reportRepository.findByStudentIdOrderBySubmittedAtDesc(studentId);
    }

    /**
     * Get all reports for a student with specific status
     */
    public List<Report> getStudentReportsByStatus(Long studentId, String status) {
        return reportRepository.findByStudentAndStatus(studentId, status);
    }

    /**
     * Get all reports with specific status (staff/professional view)
     */
    public List<Report> getReportsByStatus(String status) {
        return reportRepository.findByStatus(status);
    }

    /**
     * Get all reports with specific urgency
     */
    public List<Report> getReportsByUrgency(String urgency) {
        return reportRepository.findByUrgencyOrderBySubmittedAtDesc(urgency);
    }

    /**
     * Get all open reports (pending, in_progress, reviewed, scheduled)
     */
    public List<Report> getOpenReports() {
        return reportRepository.findOpenReportsOrderedByUrgency();
    }

    /**
     * Get resolved reports (history)
     */
    public List<Report> getResolvedReports() {
        return reportRepository.findResolvedReports();
    }

    // ==================== UPDATE Operations ====================
    /**
     * Update report status
     */
    public Report updateReportStatus(Long reportId, String newStatus) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            report.setStatus(newStatus);
            report.setUpdatedAt(LocalDateTime.now());
            return reportRepository.save(report);
        }
        return null;
    }

    /**
     * Update report with resolution details
     */
    public Report resolveReport(Long reportId, String resolutionNotes) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            report.setStatus("resolved");
            report.setResolutionNotes(resolutionNotes);
            report.setResolvedAt(LocalDateTime.now());
            report.setUpdatedAt(LocalDateTime.now());
            return reportRepository.save(report);
        }
        return null;
    }

    /**
     * Close a report
     */
    public Report closeReport(Long reportId, String resolutionNotes) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            report.setStatus("closed");
            report.setResolutionNotes(resolutionNotes);
            report.setResolvedAt(LocalDateTime.now());
            report.setUpdatedAt(LocalDateTime.now());
            return reportRepository.save(report);
        }
        return null;
    }

    /**
     * Update report urgency
     */
    public Report updateReportUrgency(Long reportId, String newUrgency) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            report.setUrgency(newUrgency);
            report.setUpdatedAt(LocalDateTime.now());
            return reportRepository.save(report);
        }
        return null;
    }

    // ==================== DELETE Operations ====================
    /**
     * Delete report by ID
     */
    public void deleteReport(Long reportId) {
        reportRepository.deleteById(reportId);
    }

    // ==================== REPORTING & ANALYTICS ====================
    /**
     * Get report statistics for dashboard
     */
    public Map<String, Long> getReportStatistics() {
        return Map.of(
            "pending", reportRepository.countByStatus("pending"),
            "in_progress", reportRepository.countByStatus("in_progress"),
            "reviewed", reportRepository.countByStatus("reviewed"),
            "resolved", reportRepository.countByStatus("resolved"),
            "closed", reportRepository.countByStatus("closed"),
            "low_urgency", reportRepository.countByUrgency("low"),
            "medium_urgency", reportRepository.countByUrgency("medium"),
            "high_urgency", reportRepository.countByUrgency("high"),
            "critical_urgency", reportRepository.countByUrgency("critical")
        );
    }

    /**
     * Get report statistics for a specific student
     */
    public Map<String, Long> getStudentReportStatistics(Long studentId) {
        return Map.of(
            "total_reports", reportRepository.countByStudentId(studentId),
            "pending", reportRepository.countByStudentAndStatus(studentId, "pending"),
            "in_progress", reportRepository.countByStudentAndStatus(studentId, "in_progress"),
            "resolved", reportRepository.countByStudentAndStatus(studentId, "resolved"),
            "closed", reportRepository.countByStudentAndStatus(studentId, "closed")
        );
    }

    /**
     * Get reports within a date range (for reporting)
     */
    public List<Report> getReportsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Get student reports within a date range
     */
    public List<Report> getStudentReportsInDateRange(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.findByStudentAndDateRange(studentId, startDate, endDate);
    }

    /**
     * Get report history for a student (resolved and closed reports)
     */
    public List<Report> getStudentReportHistory(Long studentId) {
        List<Report> allReports = reportRepository.findByStudentIdOrderBySubmittedAtDesc(studentId);
        return allReports.stream()
            .filter(r -> "resolved".equals(r.getStatus()) || "closed".equals(r.getStatus()))
            .collect(Collectors.toList());
    }

    /**
     * Convert reports to map format for JSON responses
     */
    public Map<String, Object> reportToMap(Report report) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", report.getId());
        map.put("type", report.getType());
        map.put("description", report.getDescription());
        map.put("status", report.getStatus());
        map.put("urgency", report.getUrgency());
        map.put("submittedAt", report.getSubmittedAt().toString());
        map.put("updatedAt", report.getUpdatedAt().toString());
        
        if (report.getStudent() != null) {
            map.put("studentId", report.getStudent().getId());
            map.put("studentName", report.getStudent().getName());
        }
        
        if ("resolved".equals(report.getStatus()) || "closed".equals(report.getStatus())) {
            map.put("resolutionNotes", report.getResolutionNotes());
            map.put("resolvedAt", report.getResolvedAt() != null ? report.getResolvedAt().toString() : null);
        }
        
        return map;
    }

    /**
     * Convert list of reports to map format
     */
    public List<Map<String, Object>> reportsToMapList(List<Report> reports) {
        return reports.stream()
            .map(this::reportToMap)
            .collect(Collectors.toList());
    }
}
