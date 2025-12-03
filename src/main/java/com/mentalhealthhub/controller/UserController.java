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
        
        return "users/manage-users";
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
        
        return "users/user-detail";
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
}
