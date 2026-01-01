package com.mentalhealthhub.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.AppointmentStatus;
import com.mentalhealthhub.model.Assessment;
import com.mentalhealthhub.model.Report;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.AppointmentRepository;
import com.mentalhealthhub.repository.AssessmentRepository;
import com.mentalhealthhub.repository.ReportRepository;
import com.mentalhealthhub.repository.UserRepository;
import com.mentalhealthhub.service.AssessmentService;
import com.mentalhealthhub.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

    private final ReportRepository reportRepository;
    private final AppointmentRepository appointmentRepository;
    private final AssessmentService assessmentService;
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final UserService userService;

    public PageController(ReportRepository reportRepository, AppointmentRepository appointmentRepository,
            AssessmentService assessmentService, UserRepository userRepository,
            AssessmentRepository assessmentRepository, UserService userService) {
        this.reportRepository = reportRepository;
        this.appointmentRepository = appointmentRepository;
        this.assessmentService = assessmentService;
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
        this.userService = userService;
    }

    @GetMapping("/settings")
    public String settings(HttpSession session, Model model,
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String error) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Refresh user from database to get latest data
        user = userRepository.findById(user.getId()).orElse(user);
        session.setAttribute("user", user); // Update session with fresh data

        if (success != null) {
            model.addAttribute("success", success);
        }
        if (error != null) {
            model.addAttribute("error", error);
        }

        model.addAttribute("user", user);
        model.addAttribute("page", "settings");
        model.addAttribute("title", "Settings");

        return "layout";
    }

    @PostMapping("/settings/update-profile")
    public String updateProfile(@RequestParam String name,
            @RequestParam String email,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Get fresh user from database
            user = userRepository.findById(user.getId()).orElse(user);

            // Update profile
            User updatedUser = userService.updateProfile(user, name, email);

            // Update session with new user data
            session.setAttribute("user", updatedUser);

            return "redirect:/settings?success=Profile updated successfully";
        } catch (IllegalArgumentException e) {
            return "redirect:/settings?error=" + e.getMessage();
        } catch (Exception e) {
            return "redirect:/settings?error=Failed to update profile: " + e.getMessage();
        }
    }

    @PostMapping("/settings/change-password")
    public String changePassword(@RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Validate password match
            if (!newPassword.equals(confirmPassword)) {
                return "redirect:/settings?error=New passwords do not match";
            }

            // Get fresh user from database
            user = userRepository.findById(user.getId()).orElse(user);

            // Verify current password
            if (!userService.verifyPassword(user, currentPassword)) {
                return "redirect:/settings?error=Current password is incorrect";
            }

            // Update password
            userService.updatePassword(user, newPassword);

            return "redirect:/settings?success=Password changed successfully";
        } catch (IllegalArgumentException e) {
            return "redirect:/settings?error=" + e.getMessage();
        } catch (Exception e) {
            return "redirect:/settings?error=Failed to change password: " + e.getMessage();
        }
    }

    @GetMapping("/notifications")
    public String notifications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        // Show staff-specific notifications page for staff users
        if (user.getRole().name().equals("STAFF")) {
            model.addAttribute("page", "notifications/staff-notifications");
        } else {
            model.addAttribute("page", "notifications/notifications");
        }

        model.addAttribute("title", "Notifications");
        model.addAttribute("activePage", "notifications");
        return "layout";
    }

    @GetMapping("/professional/reports")
    public String professionalReports(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Optional: restrict to professionals only
        if (user.getRole() != UserRole.PROFESSIONAL) {
            return "redirect:/dashboard";
        }

        List<Report> reports = reportRepository.findAll();
        // Exclude resolved reports from the "all" view
        List<Report> activeReports = reports.stream()
                .filter(r -> !"resolved".equalsIgnoreCase(r.getStatus()))
                .toList();
        long allCount = activeReports.size();
        long pendingCount = reports.stream().filter(r -> "pending".equalsIgnoreCase(r.getStatus())).count();
        long reviewedCount = reports.stream().filter(r -> "reviewed".equalsIgnoreCase(r.getStatus())).count();
        long scheduledCount = reports.stream().filter(r -> "scheduled".equalsIgnoreCase(r.getStatus())).count();
        long resolvedCount = reports.stream().filter(r -> "resolved".equalsIgnoreCase(r.getStatus())).count();
        long urgentCount = reports.stream().filter(r -> "urgent".equalsIgnoreCase(r.getUrgency())).count();

        model.addAttribute("user", user);
        model.addAttribute("reports", activeReports);
        model.addAttribute("allReports", reports);
        model.addAttribute("allCount", allCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("reviewedCount", reviewedCount);
        model.addAttribute("scheduledCount", scheduledCount);
        model.addAttribute("resolvedCount", resolvedCount);
        model.addAttribute("urgentCount", urgentCount);
        model.addAttribute("page", "professional/review-reports");
        model.addAttribute("title", "Review Reports");

        return "layout";
    }

    @GetMapping("/professional/reports/{id}")
    public String reportDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole() != UserRole.PROFESSIONAL) {
            return "redirect:/dashboard";
        }

        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {
            return "redirect:/404";
        }

        // Auto-update status to "reviewed" when professional views a pending report
        if ("pending".equals(report.getStatus())) {
            report.setStatus("reviewed");
            reportRepository.save(report);
            System.out.println("Status auto-updated from 'pending' to 'reviewed'");
        }

        // Fetch appointment if report status is scheduled
        Appointment appointment = null;
        Appointment suggestedAppointment = null;
        
        System.out.println("\n========================================");
        System.out.println("=== DEBUG: Professional View Report Detail ===");
        System.out.println("========================================");
        System.out.println("User: " + (user != null ? user.getName() + " (" + user.getId() + ")" : "null"));
        System.out.println("Report ID: " + report.getId());
        System.out.println("Report Type: " + report.getType());
        System.out.println("Report Status: " + report.getStatus());
        System.out.println("Report Urgency: " + report.getUrgency());
        System.out.println("Student: " + (report.getStudent() != null ? report.getStudent().getName() : "null"));
        System.out.println("Description: " + (report.getDescription() != null ? report.getDescription().substring(0, Math.min(50, report.getDescription().length())) + "..." : "null"));
        System.out.println("--------");
        
        if ("scheduled".equals(report.getStatus())) {
            appointment = appointmentRepository.findByReportId(report.getId());
            System.out.println("Status is SCHEDULED");
            if (appointment != null) {
                System.out.println("  Appointment ID: " + appointment.getId());
                System.out.println("  Appointment Date: " + appointment.getAppointmentDate());
                System.out.println("  Time Slot: " + appointment.getTimeSlotStart() + " - " + appointment.getTimeSlotEnd());
                System.out.println("  Appointment Status: " + appointment.getStatus());
            } else {
                System.out.println("  WARNING: No appointment found for scheduled report!");
            }
        } else if ("reviewed".equals(report.getStatus())) {
            System.out.println("Status is REVIEWED");
            System.out.println("  Searching for STUDENT_PROPOSED appointments...");
            suggestedAppointment = appointmentRepository.findByReportIdAndStatus(report.getId(), AppointmentStatus.STUDENT_PROPOSED);
            if (suggestedAppointment != null) {
                System.out.println("  Student Proposed Appointment ID: " + suggestedAppointment.getId());
                System.out.println("  Original Proposed Date: " + suggestedAppointment.getAppointmentDate());
                System.out.println("  Student's Counter-Proposal Date: " + suggestedAppointment.getSuggestedAppointmentDate());
                System.out.println("  Student's Counter-Proposal Time: " + suggestedAppointment.getSuggestedTimeSlotStart() + " - " + suggestedAppointment.getSuggestedTimeSlotEnd());
            } else {
                System.out.println("  No student counter-proposal found");
            }
        } else if ("pending".equals(report.getStatus())) {
            System.out.println("Status is PENDING - Professional can schedule appointment");
        } else if ("in_progress".equals(report.getStatus())) {
            System.out.println("Status is IN_PROGRESS");
        } else if ("resolved".equals(report.getStatus())) {
            System.out.println("Status is RESOLVED");
            System.out.println("  Resolution Notes: " + (report.getResolutionNotes() != null ? report.getResolutionNotes().substring(0, Math.min(50, report.getResolutionNotes().length())) + "..." : "null"));
            System.out.println("  Resolved At: " + report.getResolvedAt());
        } else if ("closed".equals(report.getStatus())) {
            System.out.println("Status is CLOSED");
        } else {
            System.out.println("Status is UNKNOWN: " + report.getStatus());
        }
        System.out.println("========================================\n");

        model.addAttribute("user", user);
        model.addAttribute("report", report);
        model.addAttribute("student", report.getStudent());
        if (appointment != null) {
            model.addAttribute("appointment", appointment);
        }
        model.addAttribute("suggestedAppointment", suggestedAppointment);
        model.addAttribute("page", "professional/report-detail");
        model.addAttribute("title", "Report Detail");
        return "layout";
    }

    @PostMapping("/professional/appointments/book")
    public String bookAppointment(@RequestParam Long reportId,
            @RequestParam String appointmentDate,
            @RequestParam String appointmentTime,
            @RequestParam(required = false) String notes,
            HttpSession session) {
        User professional = (User) session.getAttribute("user");
        if (professional == null) {
            return "redirect:/login";
        }

        Report report = reportRepository.findById(reportId).orElse(null);
        if (report == null) {
            return "redirect:/404";
        }

        try {
            // Parse date and time (format: YYYY-MM-DD and HH:MM)
            LocalDateTime appointmentDateTime = LocalDateTime.parse(appointmentDate + "T" + appointmentTime);

            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setStudent(report.getStudent());
            appointment.setProfessional(professional);
            appointment.setAppointmentDate(appointmentDateTime.toLocalDate());
            appointment.setTimeSlotStart(appointmentDateTime.toLocalTime());
            appointment.setTimeSlotEnd(appointmentDateTime.plusHours(1).toLocalTime());
            appointment.setStatus(AppointmentStatus.PENDING);
            appointment.setNotes(notes);
            appointment.setCreatedAt(LocalDateTime.now());

            appointmentRepository.save(appointment);

            // Update report status to scheduled
            report.setStatus("scheduled");
            reportRepository.save(report);

            return "redirect:/professional/reports?success=true";
        } catch (Exception e) {
            return "redirect:/professional/reports/" + reportId + "?error=true";
        }
    }

    @PostMapping("/professional/reports/{id}/resolve")
    public String resolveReport(@PathVariable Long id,
            @RequestParam String resolutionNotes,
            HttpSession session) {
        User professional = (User) session.getAttribute("user");
        if (professional == null) {
            return "redirect:/login";
        }

        if (professional.getRole() != UserRole.PROFESSIONAL) {
            return "redirect:/dashboard";
        }

        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {
            return "redirect:/404";
        }

        try {
            // Update report with resolution information
            report.setStatus("resolved");
            report.setResolutionNotes(resolutionNotes);
            report.setResolvedAt(LocalDateTime.now());
            reportRepository.save(report);

            return "redirect:/professional/reports/" + id + "?success=true";
        } catch (Exception e) {
            return "redirect:/professional/reports/" + id + "?error=true";
        }
    }

    @GetMapping("/professional/students/{studentId}/health-records")
    public String viewStudentHealthRecords(@PathVariable Long studentId, HttpSession session, Model model) {
        User professional = (User) session.getAttribute("user");
        if (professional == null || professional.getRole() != UserRole.PROFESSIONAL) {
            return "redirect:/login";
        }

        // Get student by ID (would normally come from UserRepository)
        // For now, we'll assume the student is accessible
        // In production, verify access control

        List<Assessment> assessments = assessmentService.getUserAssessments(null);

        model.addAttribute("studentId", studentId);
        model.addAttribute("assessments", assessments);
        model.addAttribute("page", "professional/student-health-records");
        model.addAttribute("title", "Student Health Records");

        return "layout";
    }

    @GetMapping("/concerns/report")
    public String reportConcern(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("page", "concerns/report");
        model.addAttribute("title", "Report Concern");
        model.addAttribute("activePage", "report");
        return "layout";
    }

    @GetMapping("/student/progress")
    public String studentProgress(HttpSession session, Model model) {
        // Redirect to /patients which now serves both professionals and staff
        return "redirect:/patients";
    }

    @GetMapping("/professional/students/{id}")
    public String viewStudentProfile(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || (user.getRole() != UserRole.PROFESSIONAL && user.getRole() != UserRole.STAFF
                && user.getRole() != UserRole.ADMIN)) {
            return "redirect:/login";
        }

        User student = userRepository.findById(id).orElse(null);
        if (student == null || student.getRole() != UserRole.STUDENT) {
            return "redirect:/patients";
        }

        List<Assessment> assessments = assessmentRepository.findAll().stream()
                .filter(a -> a.getUser() != null && a.getUser().getId().equals(id))
                .toList();

        model.addAttribute("student", student);
        model.addAttribute("assessments", assessments);
        model.addAttribute("totalAssessments", assessments.size());
        model.addAttribute("user", user);
        model.addAttribute("page", "professional/student-profile");
        model.addAttribute("title", "Student Profile");

        return "layout";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/404")
    public String notFound() {
        return "404";
    }
}

