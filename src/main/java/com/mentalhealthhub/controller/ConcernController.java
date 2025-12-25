package com.mentalhealthhub.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mentalhealthhub.model.Report;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.ReportRepository;
import com.mentalhealthhub.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ConcernController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/concerns")
    public String viewConcernForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("page", "concerns/report");
        model.addAttribute("title", "Report a Concern");

        return "layout";
    }

    @GetMapping("/concerns/list")
    public String viewMyReports(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("page", "concerns/list");
        model.addAttribute("title", "My Reports");
        model.addAttribute("activePage", "concerns");

        return "layout";
    }

    @PostMapping("/concerns/submit")
    public String submitConcern(@RequestParam(required = false, name = "concerns") String[] concerns,
            @RequestParam String description,
            @RequestParam(required = false) String urgency,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        Report report = new Report();

        if (concerns != null && concerns.length > 0) {
            report.setType(String.join(", ", concerns));
        } else {
            report.setType("Other");
        }

        report.setDescription(description);
        report.setUrgency(urgency);

        if (user != null) {
            report.setStudent(user);
        }

        reportRepository.save(report);

        // After submission, redirect student to dashboard or confirmation
        return "redirect:/dashboard";
    }

    // Staff-specific endpoints for creating reports on behalf of students
    @GetMapping("/concerns/staff-report")
    public String staffReportPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        // Check if user is staff
        if (user == null || user.getRole() != UserRole.STAFF) {
            return "redirect:/login";
        }

        model.addAttribute("page", "concerns/staff-report");
        model.addAttribute("title", "Create Student Report");
        model.addAttribute("activePage", "reports");

        return "layout";
    }

    @GetMapping("/api/students")
    @ResponseBody
    public ResponseEntity<?> getStudents() {
        // Get all students (STUDENT role users)
        List<User> students = userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.STUDENT)
                .collect(Collectors.toList());

        // Return simplified student data (id, name, email)
        List<Map<String, Object>> studentData = students.stream()
                .map(s -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", s.getId());
                    map.put("name", s.getName());
                    map.put("email", s.getEmail());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(studentData);
    }

    @GetMapping("/concerns/api/my-reports")
    @ResponseBody
    public List<Map<String, Object>> getMyReports(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return List.of();
        }

        // Get reports for the current student
        List<Report> studentReports = reportRepository.findAll().stream()
                .filter(r -> r.getStudent() != null && r.getStudent().getId().equals(user.getId()))
                .sorted((a, b) -> b.getSubmittedAt().compareTo(a.getSubmittedAt()))
                .collect(Collectors.toList());

        // Convert to map for JSON response
        return studentReports.stream().map(report -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", report.getId());
            map.put("type", report.getType());
            map.put("description", report.getDescription());
            map.put("status", report.getStatus());
            map.put("urgency", report.getUrgency());
            map.put("submittedAt", report.getSubmittedAt().toString());
            return map;
        }).collect(Collectors.toList());
    }

    @PostMapping("/concerns/staff-submit")
    public String staffSubmitConcern(@RequestParam(required = false, name = "concerns") String[] concerns,
            @RequestParam String description,
            @RequestParam(required = false) String urgency,
            @RequestParam Long studentId,
            HttpSession session) {
        User staff = (User) session.getAttribute("user");

        // Check if user is staff
        if (staff == null || staff.getRole() != UserRole.STAFF) {
            return "redirect:/login";
        }

        // Find the student
        User student = userRepository.findById(studentId).orElse(null);
        if (student == null || student.getRole() != UserRole.STUDENT) {
            return "redirect:/concerns/staff-report";
        }

        // Create report
        Report report = new Report();

        if (concerns != null && concerns.length > 0) {
            report.setType(String.join(", ", concerns));
        } else {
            report.setType("Other");
        }

        report.setDescription(description);
        report.setUrgency(urgency);
        report.setStudent(student);

        reportRepository.save(report);

        // Redirect back to staff report page
        return "redirect:/concerns/staff-report";
    }

    @GetMapping("/concerns/{id}")
    public String viewMyReportDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {
            return "redirect:/concerns/list";
        }

        // Verify that the report belongs to the current user
        if (report.getStudent() == null || !report.getStudent().getId().equals(user.getId())) {
            return "redirect:/concerns/list";
        }

        model.addAttribute("user", user);
        model.addAttribute("report", report);
        model.addAttribute("page", "concerns/report-detail");
        model.addAttribute("title", "Report Details");
        model.addAttribute("activePage", "concerns");

        return "layout";
    }
}
