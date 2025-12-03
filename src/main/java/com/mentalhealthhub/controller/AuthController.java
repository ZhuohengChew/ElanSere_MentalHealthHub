package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, 
                        @RequestParam String password,
                        @RequestParam(required = false) String role,
                        HttpSession session, 
                        Model model) {
        // For demo purposes, create or fetch user
        User user = userRepository.findByEmail(email)
            .orElseGet(() -> {
                User newUser = User.builder()
                    .email(email)
                    .password(password)
                    .name(email.split("@")[0])
                    .role(role != null ? UserRole.valueOf(role.toUpperCase()) : UserRole.STUDENT)
                    .active(true)
                    .build();
                return userRepository.save(newUser);
            });

        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole().toString());
        
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        
        String dashboardPage;
        switch (user.getRole()) {
            case STUDENT:
                dashboardPage = "dashboard/student/dashboard";
                break;
            case STAFF:
                dashboardPage = "dashboard/staff-dashboard";
                break;
            case PROFESSIONAL:
                dashboardPage = "dashboard/professional-dashboard";
                break;
            case ADMIN:
                dashboardPage = "dashboard/admin-dashboard";
                break;
            default:
                return "redirect:/login";
        }
        model.addAttribute("page", dashboardPage);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("title", "Dashboard");
        return "layout";
    }
}
