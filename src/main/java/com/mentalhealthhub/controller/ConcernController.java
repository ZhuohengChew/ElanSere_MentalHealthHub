package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.Report;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.ReportRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ConcernController {

    @Autowired
    private ReportRepository reportRepository;

    @GetMapping("/concerns")
    public String viewMyReports(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Get reports for the current student
        List<Report> studentReports = reportRepository.findAll().stream()
                .filter(r -> r.getStudent() != null && r.getStudent().getId().equals(user.getId()))
                .sorted((a, b) -> b.getSubmittedAt().compareTo(a.getSubmittedAt()))
                .collect(Collectors.toList());

        model.addAttribute("reports", studentReports);
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
}
