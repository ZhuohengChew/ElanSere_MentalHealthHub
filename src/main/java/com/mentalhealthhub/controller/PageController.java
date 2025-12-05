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

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

    private final ReportRepository reportRepository;
    private final AppointmentRepository appointmentRepository;
    private final AssessmentService assessmentService;
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;

    public PageController(ReportRepository reportRepository, AppointmentRepository appointmentRepository, AssessmentService assessmentService, UserRepository userRepository, AssessmentRepository assessmentRepository) {
        this.reportRepository = reportRepository;
        this.appointmentRepository = appointmentRepository;
        this.assessmentService = assessmentService;
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
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

        // Show staff-specific notifications page for staff users
        if (user.getRole().name().equals("STAFF")) {
            model.addAttribute("page", "notifications/staff-notifications");
        } else {
            model.addAttribute("page", "notifications");
        }
        
        model.addAttribute("title", "Notifications");
        model.addAttribute("activePage", "notifications");
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

        // Update report status to "reviewed" when professional views it
        if (report.getStatus() == null || report.getStatus().isEmpty() || report.getStatus().equals("pending")) {
            report.setStatus("reviewed");
            reportRepository.save(report);
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
        if (user == null || (user.getRole() != UserRole.PROFESSIONAL && user.getRole() != UserRole.STAFF)) {
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
