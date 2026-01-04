package com.mentalhealthhub.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import com.mentalhealthhub.repository.AppointmentRepository;
import com.mentalhealthhub.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String myPatients(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Only allow PROFESSIONAL and STAFF roles to access patients
        if (user.getRole() != UserRole.PROFESSIONAL && user.getRole() != UserRole.STAFF && user.getRole() != UserRole.ADMIN) {
            return "redirect:/dashboard";
        }

        // Get all students from the system (they are all patients)
        List<User> allStudents = userRepository.findAll().stream()
            .filter(u -> u.getRole() == UserRole.STUDENT)
            .collect(Collectors.toList());

        // Get all appointments for this professional to show statistics (only relevant for professionals)
        List<Appointment> appointments = user.getRole() == UserRole.PROFESSIONAL
            ? appointmentRepository.findByProfessional(user)
            : new java.util.ArrayList<>();

        // Create a map of student ID to appointment count for easy lookup in template
        Map<Long, Long> appointmentCounts = new HashMap<>();
        for (User student : allStudents) {
            long count = appointments.stream()
                .filter(apt -> apt.getStudent() != null && apt.getStudent().getId().equals(student.getId()))
                .count();
            appointmentCounts.put(student.getId(), count);
        }

        // Show all students to PROFESSIONALs and STAFF (do not filter by existing appointments)
        List<User> patientsList = allStudents;

        model.addAttribute("patients", patientsList);
        model.addAttribute("appointmentCounts", appointmentCounts);
        model.addAttribute("user", user);
        
        model.addAttribute("page", "patients/list");
        model.addAttribute("title", user.getRole() == UserRole.STAFF ? "Track Student Progress" : "My Patients");

        return "layout";
    }
}
