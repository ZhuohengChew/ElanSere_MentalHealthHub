package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.*;
import com.mentalhealthhub.repository.ReportRepository;
import com.mentalhealthhub.repository.AppointmentRepository;
import com.mentalhealthhub.service.AssessmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class PageController {

    private final ReportRepository reportRepository;
    private final AppointmentRepository appointmentRepository;
    private final AssessmentService assessmentService;

    public PageController(ReportRepository reportRepository, AppointmentRepository appointmentRepository, AssessmentService assessmentService) {
        this.reportRepository = reportRepository;
        this.appointmentRepository = appointmentRepository;
        this.assessmentService = assessmentService;
    }

    @GetMapping("/settings")
    public String settings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        model.addAttribute("page", "settings");
        model.addAttribute("title", "Settings");

        return "layout";
    }

    @GetMapping("/notifications")
    public String notifications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        model.addAttribute("page", "notifications");
        model.addAttribute("title", "Notifications");
        return "layout";
    }

    @GetMapping("/self-care")
    public String selfCare(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("page", "self-care");
        model.addAttribute("title", "Self-Care Tools");
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
        long allCount = reports.size();
        long pendingCount = reports.stream().filter(r -> "pending".equalsIgnoreCase(r.getStatus())).count();
        long reviewedCount = reports.stream().filter(r -> "reviewed".equalsIgnoreCase(r.getStatus())).count();
        long scheduledCount = reports.stream().filter(r -> "scheduled".equalsIgnoreCase(r.getStatus())).count();
        long urgentCount = reports.stream().filter(r -> "urgent".equalsIgnoreCase(r.getUrgency())).count();

        model.addAttribute("user", user);
        model.addAttribute("reports", reports);
        model.addAttribute("allCount", allCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("reviewedCount", reviewedCount);
        model.addAttribute("scheduledCount", scheduledCount);
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

        model.addAttribute("user", user);
        model.addAttribute("report", report);
        model.addAttribute("student", report.getStudent());
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
            appointment.setAppointmentDate(appointmentDateTime);
            appointment.setStatus(AppointmentStatus.SCHEDULED);
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
    @GetMapping("/modules")
    public String modules(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("page", "modules");
        model.addAttribute("title", "Educational Modules");
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
