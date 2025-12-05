package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

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
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("page", "progress/student-progress");
        model.addAttribute("title", "Track Student Progress");
        model.addAttribute("activePage", "student-progress");
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
