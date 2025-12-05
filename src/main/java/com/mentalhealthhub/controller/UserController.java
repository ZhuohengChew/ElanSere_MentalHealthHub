package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listUsers(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().toString().equals("ADMIN")) {
            return "redirect:/login";
        }

        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", user);

        model.addAttribute("page", "users/manage-users");
        model.addAttribute("title", "Manage Users");
        
        return "layout";
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        User targetUser = userRepository.findById(id).orElse(null);
        if (targetUser == null) {
            return "redirect:/users";
        }

        model.addAttribute("user", targetUser);
        model.addAttribute("currentUser", user);
        
        model.addAttribute("page", "users/user-detail");
        model.addAttribute("title", "User Details");

        return "layout";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().toString().equals("ADMIN")) {
            return "redirect:/login";
        }

        User targetUser = userRepository.findById(id).orElse(null);
        if (targetUser != null) {
            targetUser.setActive(false);
            userRepository.save(targetUser);
        }

        return "redirect:/users";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam String name,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String role,
                          HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.getRole().toString().equals("ADMIN")) {
            return "redirect:/login";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/users?error=User with this email already exists";
        }

        User newUser = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(com.mentalhealthhub.model.UserRole.valueOf(role.toUpperCase()))
                .active(true)
                .build();

        userRepository.save(newUser);
        return "redirect:/users";
    }
}
