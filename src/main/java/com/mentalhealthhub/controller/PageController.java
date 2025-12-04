package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.User;
import jakarta.servlet.http.HttpSession;

import org.aspectj.internal.lang.annotation.ajcDeclarePrecedence;
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

        model.addAttribute("page", "notifications");
        model.addAttribute("title", "Notifications");
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
