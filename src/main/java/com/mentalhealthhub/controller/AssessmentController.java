package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.Assessment;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.AssessmentRepository;
import com.mentalhealthhub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listAssessments(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Assessment> assessments = assessmentRepository.findByUserOrderByCreatedAtDesc(user);
        model.addAttribute("assessments", assessments);
        model.addAttribute("user", user);
        
        return "assessments/list";
    }

    @GetMapping("/new")
    public String newAssessment(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "assessments/new";
    }

    @PostMapping("/save")
    public String saveAssessment(@RequestParam String title,
                                 @RequestParam String description,
                                 @RequestParam String content,
                                 HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Assessment assessment = Assessment.builder()
            .user(user)
            .title(title)
            .description(description)
            .content(content)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        assessmentRepository.save(assessment);
        return "redirect:/assessments";
    }

    @GetMapping("/{id}")
    public String viewAssessment(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Assessment assessment = assessmentRepository.findById(id).orElse(null);
        if (assessment == null) {
            return "redirect:/assessments";
        }

        model.addAttribute("assessment", assessment);
        model.addAttribute("user", user);
        
        return "assessments/view";
    }
}
