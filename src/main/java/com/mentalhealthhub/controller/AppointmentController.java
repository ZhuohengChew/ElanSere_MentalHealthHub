package com.mentalhealthhub.controller;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.AppointmentRepository;
import com.mentalhealthhub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listAppointments(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Appointment> appointments = appointmentRepository.findByStudent(user);
        model.addAttribute("appointments", appointments);
        model.addAttribute("user", user);
        
        model.addAttribute("page", "appointments/list");
        model.addAttribute("title", "My Appointments");

        return "layout";
    }

    @GetMapping("/book")
    public String bookAppointment(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<User> professionals = userRepository.findAll().stream()
            .filter(u -> u.getRole().toString().equals("PROFESSIONAL"))
            .toList();

        model.addAttribute("professionals", professionals);
        model.addAttribute("user", user);

        model.addAttribute("page", "appointments/book");
        model.addAttribute("title", "Book Appointment");
        
        return "layout";
    }

    @PostMapping("/save")
    public String saveAppointment(@RequestParam Long professionalId,
                                  @RequestParam String appointmentDate,
                                  @RequestParam(required = false) String notes,
                                  HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        User professional = userRepository.findById(professionalId).orElse(null);
        if (professional == null) {
            return "redirect:/appointments/book";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(appointmentDate, formatter);

        Appointment appointment = Appointment.builder()
            .student(user)
            .professional(professional)
            .appointmentDate(dateTime)
            .notes(notes)
            .createdAt(LocalDateTime.now())
            .build();

        appointmentRepository.save(appointment);
        return "redirect:/appointments";
    }
}
