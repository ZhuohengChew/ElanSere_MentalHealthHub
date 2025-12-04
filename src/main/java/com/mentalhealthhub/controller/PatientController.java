package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.AppointmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping
    public String myPatients(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Get all appointments for this professional
        List<Appointment> appointments = appointmentRepository.findByProfessional(user);
        
        // Extract unique students (patients) from appointments
        List<User> patients = appointments.stream()
            .map(Appointment::getStudent)
            .distinct()
            .collect(Collectors.toList());

        model.addAttribute("patients", patients);
        model.addAttribute("appointments", appointments);
        model.addAttribute("user", user);
        
        model.addAttribute("page", "patients/list");
        model.addAttribute("title", "My Patients");

        return "layout";
    }
}
