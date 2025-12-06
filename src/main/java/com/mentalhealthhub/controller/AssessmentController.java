package com.mentalhealthhub.controller;

import com.mentalhealthhub.dto.AssessmentQuestionDTO;
import com.mentalhealthhub.dto.AssessmentResultDTO;
import com.mentalhealthhub.model.Assessment;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.service.AssessmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private com.mentalhealthhub.repository.AssessmentRepository assessmentRepository;

    @GetMapping
    public String listAssessments(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Assessment> assessments = assessmentService.getUserAssessments(user);
        model.addAttribute("assessments", assessments);
        model.addAttribute("user", user);

        model.addAttribute("page", "assessments/list");
        model.addAttribute("title", "My Assessments");
        model.addAttribute("activePage", "assessments");

        return "layout";
    }

    @GetMapping("/new")
    public String startNewAssessment(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Initialize new assessment session
        session.setAttribute("currentQuestion", 1);
        session.setAttribute("answers", new ArrayList<Integer>());

        return "redirect:/assessments/question/1";
    }

    @GetMapping("/question/{questionNumber}")
    public String showQuestion(@PathVariable int questionNumber,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (questionNumber < 1 || questionNumber > 8) {
            return "redirect:/assessments/new";
        }

        AssessmentQuestionDTO question = assessmentService.getQuestion(questionNumber);
        if (question == null) {
            return "redirect:/assessments/new";
        }

        model.addAttribute("question", question);
        model.addAttribute("currentQuestion", questionNumber);
        model.addAttribute("progressPercentage", (questionNumber - 1) * 12.5); // 100/8 per question
        model.addAttribute("totalQuestions", 8);
        model.addAttribute("user", user);

        model.addAttribute("page", "assessments/question");
        model.addAttribute("title", "Self-Assessment - Question " + questionNumber);
        model.addAttribute("activePage", "assessments");

        return "layout";
    }

    @PostMapping("/answer")
    public String submitAnswer(@RequestParam("answer") Integer answer,
            @RequestParam("questionNumber") Integer questionNumber,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        List<Integer> answers = (List<Integer>) session.getAttribute("answers");
        if (answers == null) {
            answers = new ArrayList<>();
        }

        // Store answer (adjust list size if needed)
        while (answers.size() < questionNumber) {
            answers.add(0);
        }
        answers.set(questionNumber - 1, answer);
        session.setAttribute("answers", answers);

        // Move to next question or complete
        if (questionNumber < 8) {
            return "redirect:/assessments/question/" + (questionNumber + 1);
        } else {
            return "redirect:/assessments/complete";
        }
    }

    @GetMapping("/complete")
    public String completeAssessment(HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        List<Integer> answers = (List<Integer>) session.getAttribute("answers");

        if (answers == null || answers.size() != 8) {
            return "redirect:/assessments/new";
        }

        // Save assessment
        Assessment assessment = assessmentService.saveAssessment(user, answers);
        AssessmentResultDTO result = assessmentService.getAssessmentResult(assessment);

        // Clear session
        session.removeAttribute("answers");
        session.removeAttribute("currentQuestion");

        model.addAttribute("result", result);
        model.addAttribute("assessment", assessment);
        model.addAttribute("user", user);

        model.addAttribute("page", "assessments/result");
        model.addAttribute("title", "Assessment Results");
        model.addAttribute("activePage", "assessments");

        return "layout";
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

        // Allow viewing if:
        // 1. User is the student who took the assessment
        // 2. User is a PROFESSIONAL (can view any student's assessment)
        // 3. User is ADMIN or STAFF
        if (!assessment.getUser().getId().equals(user.getId()) &&
            user.getRole() != UserRole.PROFESSIONAL &&
            user.getRole() != UserRole.ADMIN &&
            user.getRole() != UserRole.STAFF) {
            return "redirect:/assessments";
        }

        model.addAttribute("assessment", assessment);
        model.addAttribute("user", user);

        model.addAttribute("page", "assessments/view");
        model.addAttribute("title", "View Assessment");
        model.addAttribute("activePage", "assessments");

        return "layout";
    }
}
