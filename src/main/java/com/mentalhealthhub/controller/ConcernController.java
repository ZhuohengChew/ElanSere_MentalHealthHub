package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.Report;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.ReportRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConcernController {

    @Autowired
    private ReportRepository reportRepository;

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
