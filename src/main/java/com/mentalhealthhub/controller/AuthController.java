package com.mentalhealthhub.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.AppointmentStatus;
import com.mentalhealthhub.model.EducationalModule;
import com.mentalhealthhub.model.Report;
import com.mentalhealthhub.model.SelfCare;
import com.mentalhealthhub.model.SelfCareType;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.AppointmentRepository;
import com.mentalhealthhub.repository.EducationalModuleRepository;
import com.mentalhealthhub.repository.ModuleProgressRepository;
import com.mentalhealthhub.repository.ReportRepository;
import com.mentalhealthhub.repository.SelfCareRepository;
import com.mentalhealthhub.repository.UserRepository;
import com.mentalhealthhub.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SelfCareRepository selfCareRepository;

    @Autowired
    private EducationalModuleRepository educationalModuleRepository;

    @Autowired
    private ModuleProgressRepository moduleProgressRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String role,
            HttpSession session,
            Model model) {
        try {
            // Authenticate user
            Optional<User> userOpt = userService.authenticateUser(email, password);

            if (userOpt.isEmpty()) {
                model.addAttribute("error", "Invalid email or password");
                return "auth/login";
            }

            User user = userOpt.get();

            // If role is specified, verify it matches user's role
            if (role != null && !role.isEmpty()) {
                UserRole selectedRole = UserRole.valueOf(role.toUpperCase());
                if (user.getRole() != selectedRole) {
                    model.addAttribute("error", "Invalid role for this account. Please select the correct role.");
                    return "auth/login";
                }
            }

            // Set session attributes
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole().toString());

            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String name,
            @RequestParam String role,
            HttpSession session,
            Model model) {
        try {
            // Validate password match
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match");
                model.addAttribute("email", email);
                model.addAttribute("name", name);
                return "auth/register";
            }

            // Validate password length
            if (password.length() < 6) {
                model.addAttribute("error", "Password must be at least 6 characters long");
                model.addAttribute("email", email);
                model.addAttribute("name", name);
                return "auth/register";
            }

            // Validate role
            UserRole userRole;
            try {
                userRole = UserRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Invalid role selected");
                model.addAttribute("email", email);
                model.addAttribute("name", name);
                return "auth/register";
            }

            // Register user
            User user = userService.registerUser(email, password, name, userRole);

            // Auto-login after registration
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole().toString());

            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            model.addAttribute("name", name);
            return "auth/register";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("email", email);
            model.addAttribute("name", name);
            return "auth/register";
        }
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
                // ===== Student dashboard dynamic data =====
                // 1. Mood this week (based on self-care mood tracking)
                LocalDate todayDate = LocalDate.now();
                LocalDate weekAgo = todayDate.minusDays(6); // last 7 days including today
                List<SelfCare> moodEntries = selfCareRepository
                        .findByUserAndTypeAndActivityDateBetween(user, SelfCareType.MOOD, weekAgo, todayDate);

                if (!moodEntries.isEmpty()) {
                    double averageMoodScore = moodEntries.stream()
                            .map(SelfCare::getMood)
                            .filter(m -> m != null && !m.isEmpty())
                            .mapToInt(this::getMoodScore)
                            .average()
                            .orElse(0);

                    String moodLabel = getMoodLabel(averageMoodScore);
                    String moodEmoji = getMoodEmoji(averageMoodScore);

                    model.addAttribute("hasMoodData", true);
                    model.addAttribute("moodLabel", moodLabel);
                    model.addAttribute("moodEmoji", moodEmoji);
                    model.addAttribute("moodDescription", "Based on your mood check-ins this week");
                } else {
                    model.addAttribute("hasMoodData", false);
                }

                // 2. Educational modules progress
                List<EducationalModule> activeModules = educationalModuleRepository
                        .findByActiveTrueOrderByCreatedAtDesc();
                int totalModules = activeModules.size();
                long completedModules = moduleProgressRepository.countByUserAndCompletedTrue(user);
                int remainingModules = Math.max(0, totalModules - (int) completedModules);

                model.addAttribute("completedModules", completedModules);
                model.addAttribute("totalModules", totalModules);
                model.addAttribute("remainingModules", remainingModules);

                // 3. Next appointment
                LocalDateTime now = LocalDateTime.now();
                List<Appointment> studentAppointments = appointmentRepository.findByStudent(user);

                Appointment nextAppointment = studentAppointments.stream()
                        .filter(a -> a.getAppointmentDate() != null
                                && a.getAppointmentDate().isAfter(now)
                                && (a.getStatus() == null || a.getStatus() == AppointmentStatus.SCHEDULED))
                        .sorted(Comparator.comparing(Appointment::getAppointmentDate))
                        .findFirst()
                        .orElse(null);

                if (nextAppointment != null) {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

                    model.addAttribute("hasNextAppointment", true);
                    model.addAttribute("nextAppointmentDate",
                            nextAppointment.getAppointmentDate().format(dateFormatter));
                    model.addAttribute("nextAppointmentTime",
                            nextAppointment.getAppointmentDate().format(timeFormatter));
                    String professionalName = (nextAppointment.getProfessional() != null
                            && nextAppointment.getProfessional().getName() != null)
                                    ? nextAppointment.getProfessional().getName()
                                    : "Your counsellor";
                    model.addAttribute("nextAppointmentProfessional", professionalName);
                } else {
                    model.addAttribute("hasNextAppointment", false);
                }

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
                        .filter(a -> a.getAppointmentDate() != null
                                && a.getAppointmentDate().toLocalDate().isEqual(today))
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

    // ===== Helper methods for student dashboard =====
    private int getMoodScore(String mood) {
        if (mood == null) {
            return 50;
        }
        switch (mood.toLowerCase()) {
            case "great":
                return 90;
            case "good":
                return 75;
            case "okay":
                return 55;
            case "low":
                return 35;
            case "struggling":
                return 15;
            default:
                return 50;
        }
    }

    private String getMoodLabel(double score) {
        if (score >= 80) {
            return "Great Mood";
        } else if (score >= 60) {
            return "Good Mood";
        } else if (score >= 40) {
            return "Okay Mood";
        } else if (score >= 20) {
            return "Low Mood";
        } else {
            return "Struggling";
        }
    }

    private String getMoodEmoji(double score) {
        if (score >= 80) {
            return "😄";
        } else if (score >= 60) {
            return "😊";
        } else if (score >= 40) {
            return "😐";
        } else if (score >= 20) {
            return "😔";
        } else {
            return "😢";
        }
    }

}
