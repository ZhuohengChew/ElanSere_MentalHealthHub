package com.mentalhealthhub.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.AppointmentStatus;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.AppointmentRepository;
import com.mentalhealthhub.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

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

    @GetMapping("/my-schedule")
    public String mySchedule(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

// Get all appointments where user is either student or professional
// Pass the same user object for BOTH parameters
List<Appointment> allAppointments = appointmentRepository.findByStudentOrProfessional(user, user);
        
        model.addAttribute("appointments", allAppointments);
        model.addAttribute("user", user);
        model.addAttribute("page", "appointments/schedule");
        model.addAttribute("title", "My Complete Schedule");

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
            .status(AppointmentStatus.SCHEDULED)
            .createdAt(LocalDateTime.now())
            .build();

        appointmentRepository.save(appointment);
        return "redirect:/appointments/my-schedule";
    }

    @GetMapping("/api/my-schedule")
    @ResponseBody
    public List<Map<String, Object>> myScheduleApi(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return List.of();
        }

// Get all appointments where user is either student or professional
// Pass the same user object for BOTH parameters
List<Appointment> allAppointments = appointmentRepository.findByStudentOrProfessional(user, user);
        
        return allAppointments.stream().map(apt -> Map.ofEntries(
            Map.entry("id", apt.getId()),
            Map.entry("appointmentDate", apt.getAppointmentDate().toString()),
            Map.entry("status", apt.getStatus().toString()),
            Map.entry("notes", apt.getNotes() != null ? apt.getNotes() : ""),
            Map.entry("student", Map.ofEntries(
                Map.entry("id", apt.getStudent().getId()),
                Map.entry("name", apt.getStudent().getName()),
                Map.entry("email", apt.getStudent().getEmail())
            )),
            Map.entry("professional", Map.ofEntries(
                Map.entry("id", apt.getProfessional().getId()),
                Map.entry("name", apt.getProfessional().getName())
            ))
        )).toList();
    }

    @GetMapping("/api/my-appointments")
    @ResponseBody
    public List<Map<String, Object>> myAppointmentsApi(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return List.of();
        }

        List<Appointment> appointments = appointmentRepository.findByStudent(user);
        return appointments.stream().map(apt -> Map.ofEntries(
            Map.entry("id", apt.getId()),
            Map.entry("appointmentDate", apt.getAppointmentDate().toString()),
            Map.entry("status", apt.getStatus().toString()),
            Map.entry("notes", apt.getNotes() != null ? apt.getNotes() : ""),
            Map.entry("student", Map.ofEntries(
                Map.entry("id", apt.getStudent().getId()),
                Map.entry("name", apt.getStudent().getName()),
                Map.entry("email", apt.getStudent().getEmail())
            )),
            Map.entry("professional", Map.ofEntries(
                Map.entry("id", apt.getProfessional().getId()),
                Map.entry("name", apt.getProfessional().getName())
            ))
        )).toList();
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Appointment not found"));
        }

        // Check if user is either the student or professional for this appointment
        if (!appointment.getStudent().getId().equals(user.getId()) && 
            !appointment.getProfessional().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized to delete this appointment"));
        }

        appointmentRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Appointment deleted successfully"));
    }
}