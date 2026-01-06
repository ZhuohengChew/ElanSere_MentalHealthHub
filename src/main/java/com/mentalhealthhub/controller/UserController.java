package com.mentalhealthhub.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mentalhealthhub.dto.CreateUserDto;
import com.mentalhealthhub.dto.UpdateUserDto;
import com.mentalhealthhub.model.AuditLog;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.AuditLogRepository;
import com.mentalhealthhub.repository.UserRepository;
import com.mentalhealthhub.service.AdminUserService;
import com.mentalhealthhub.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AdminUserService adminUserService;
    private final AuditLogRepository auditLogRepository;

    public UserController(
            UserRepository userRepository,
            UserService userService,
            AdminUserService adminUserService,
            AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.adminUserService = adminUserService;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public String listUsers(Model model, HttpSession session,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "sort", defaultValue = "name") String sort) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().toString().equals("ADMIN")) {
            return "redirect:/login";
        }

        UserRole roleEnum = null;
        if (role != null && !role.isEmpty()) {
            try {
                roleEnum = UserRole.valueOf(role.toUpperCase());
            } catch (Exception ignored) {
            }
        }

        Sort sortObj = Sort.by(Sort.Direction.ASC, "name");
        if (sort != null) {
            if (sort.equals("role"))
                sortObj = Sort.by("role");
            if (sort.equals("createdAt"))
                sortObj = Sort.by("createdAt");
        }

        Page<User> usersPage = userRepository.searchUsers(q == null ? null : q.toLowerCase(), roleEnum, active,
                PageRequest.of(page, size, sortObj));

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("query", q);
        model.addAttribute("roleFilter", role);
        model.addAttribute("activeFilter", active);
        model.addAttribute("sort", sort);

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

    @GetMapping("/{id}/audit")
    public String viewAudit(@PathVariable Long id, Model model, HttpSession session,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().toString().equals("ADMIN")) {
            return "redirect:/login";
        }

        User targetUser = userRepository.findById(id).orElse(null);
        if (targetUser == null) {
            return "redirect:/users";
        }

        Page<AuditLog> auditPage = auditLogRepository.findByUserId(id,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        model.addAttribute("targetUser", targetUser);
        model.addAttribute("auditLogs", auditPage.getContent());
        model.addAttribute("auditPage", auditPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("currentUser", user);

        model.addAttribute("page", "users/audit-log");
        model.addAttribute("title", "Audit Log - " + targetUser.getName());

        return "layout";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().toString().equals("ADMIN")) {
            return "redirect:/login";
        }

        adminUserService.changeStatus(id, false, user.getId());

        return "redirect:/users";
    }

    @PostMapping("/{id}/activate")
    public String activateUser(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().toString().equals("ADMIN")) {
            return "redirect:/login";
        }

        adminUserService.changeStatus(id, true, user.getId());
        return "redirect:/users";
    }



    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().toString().equals("ADMIN")) {
            return "redirect:/login";
        }

        String newPass = adminUserService.resetPassword(id, user.getId());
        redirectAttributes.addFlashAttribute("resetMessage", "New password: " + newPass);
        return "redirect:/users";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.getRole().toString().equals("ADMIN")) {
            return "redirect:/login";
        }

        try {
            CreateUserDto dto = new CreateUserDto();
            dto.email = email;
            dto.password = password;
            dto.name = name;
            dto.role = role.toUpperCase();
            dto.active = true;

            adminUserService.createUser(dto, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users";
        }
    }

    @PostMapping("/{id}/edit")
    public org.springframework.http.ResponseEntity<?> editUser(@PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String profilePicture,
            HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.getRole().toString().equals("ADMIN")) {
            return org.springframework.http.ResponseEntity.status(401).body(
                    new java.util.HashMap<String, String>() {{
                        put("error", "Unauthorized");
                    }});
        }

        try {
            UpdateUserDto dto = new UpdateUserDto();
            dto.name = name;
            dto.email = email;
            dto.role = role;
            dto.profilePicture = profilePicture;
            User updated = adminUserService.updateUser(id, dto, currentUser.getId());
            return org.springframework.http.ResponseEntity.ok(
                    new java.util.HashMap<String, String>() {{
                        put("success", "User updated successfully");
                        put("userName", updated.getName());
                    }});
        } catch (IllegalArgumentException e) {
            return org.springframework.http.ResponseEntity.badRequest()
                    .body(new java.util.HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }
}
