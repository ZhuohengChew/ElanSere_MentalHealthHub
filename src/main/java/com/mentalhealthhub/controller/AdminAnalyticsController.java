package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminAnalyticsController {

    private final UserRepository userRepository;

    public AdminAnalyticsController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/analytics")
    public String analytics(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return "redirect:/login";
        }

        // Refresh user
        user = userRepository.findById(user.getId()).orElse(user);
        session.setAttribute("user", user);

        model.addAttribute("user", user);
        model.addAttribute("page", "admin/analytics-report");
        model.addAttribute("title", "Analytics Dashboard");
        model.addAttribute("activePage", "analytics");

        return "layout";
    }
}
