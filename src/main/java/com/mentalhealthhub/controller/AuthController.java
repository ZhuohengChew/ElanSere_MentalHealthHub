package com.mentalhealthhub.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.Report;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.AppointmentRepository;
import com.mentalhealthhub.repository.ReportRepository;
import com.mentalhealthhub.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ReportRepository reportRepository;

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
        // Determine selected role (default to STUDENT)
        UserRole selectedRole = role != null
                ? UserRole.valueOf(role.toUpperCase())
                : UserRole.STUDENT;

        // Create or fetch user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .password(password)
                            .name(email.split("@")[0])
                            .role(selectedRole)
                            .active(true)
                            .build();
                    return userRepository.save(newUser);
                });

        // Always update user's role to match the latest selection
        user.setRole(selectedRole);
        user = userRepository.save(user);

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
                dashboardPage = "dashboard/student-dashboard";
                break;
            case STAFF:
                dashboardPage = "dashboard/staff-dashboard";
                break;
            case PROFESSIONAL:
                dashboardPage = "dashboard/professional-dashboard";
                List<Appointment> profAppointments = appointmentRepository.findByProfessional(user);
                LocalDate today = LocalDate.now();
                List<Appointment> todayAppointments = profAppointments.stream()
                    .filter(a -> a.getAppointmentDate() != null && a.getAppointmentDate().toLocalDate().isEqual(today))
                    .sorted(Comparator.comparing(Appointment::getAppointmentDate))
                    .collect(Collectors.toList());

                long todayCount = todayAppointments.size();
                long activeClients = profAppointments.stream()
                    .filter(a -> a.getStudent() != null && a.getStudent().getId() != null)
                    .map(a -> a.getStudent().getId())
                    .distinct()
                    .count();

                long sessionsCompleted = profAppointments.stream()
                    .filter(a -> a.getStatus() != null && a.getStatus().name().equals("COMPLETED"))
                    .count();

                // Show ALL students as active patients (not just those with appointments)
                List<?> patientsList = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == UserRole.STUDENT)
                    .collect(Collectors.toList());

                // Fetch all reports
                List<Report> reports = reportRepository.findAll();

                model.addAttribute("todayAppointmentsCount", todayCount);
                model.addAttribute("activeClientsCount", activeClients);
                model.addAttribute("sessionsCompleted", sessionsCompleted);
                model.addAttribute("todayAppointments", todayAppointments);
                model.addAttribute("patientsList", patientsList);
                model.addAttribute("reports", reports);
                break;
            case ADMIN:
                dashboardPage = "dashboard/admin-dashboard";
                break;
            default:
                return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("page", dashboardPage);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("title", "Dashboard");
        return "layout";
    }
    @GetMapping("/manage-users")
    public String manageUsers(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Only admins can manage users
        if (user.getRole() != UserRole.ADMIN) {
            return "redirect:/dashboard";
        }

        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("user", user);
        model.addAttribute("page", "users/manage-users");
        model.addAttribute("activePage", "users");
        model.addAttribute("title", "Manage Users");
        return "layout";
    }

    @GetMapping("/analytics")
    public String analyticsReport(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Only admins can view analytics
        if (user.getRole() != UserRole.ADMIN) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", user);
        model.addAttribute("page", "admin/analytics-report");
        model.addAttribute("activePage", "analytics");
        model.addAttribute("title", "Analytics Report");
        return "layout";
    }
    
}
