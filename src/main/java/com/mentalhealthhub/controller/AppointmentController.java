package com.mentalhealthhub.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.AppointmentStatus;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.AppointmentRepository;
import com.mentalhealthhub.repository.UserRepository;
import com.mentalhealthhub.service.AppointmentService;
import com.mentalhealthhub.util.TimeSlotUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    /**
     * Constructor-based dependency injection.
     * Spring IoC container will automatically inject the required dependencies
     * when creating an instance of AppointmentController.
     * 
     * @param appointmentRepository Repository for appointment data access
     * @param appointmentService    Service for appointment-related business logic
     * @param userRepository        Repository for user data access
     */
    public AppointmentController(AppointmentRepository appointmentRepository,
            AppointmentService appointmentService,
            UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }

    // Student View: List of appointments (approved and pending)
    @GetMapping
    public String listAppointments(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Appointment> appointments = appointmentService.getStudentAppointments(user);
        model.addAttribute("appointments", appointments);
        model.addAttribute("user", user);

        model.addAttribute("page", "appointments/list");
        model.addAttribute("title", "My Appointments");

        return "layout";
    }

    // Student: Book appointment page
    @GetMapping("/book")
    public String bookAppointment(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<User> professionals = userRepository.findAll().stream()
                .filter(u -> u.getRole().toString().equals("PROFESSIONAL"))
                .toList();

        List<TimeSlotUtil.TimeSlot> allSlots = TimeSlotUtil.generateAllTimeSlots();

        // Format minimum date as today - past time slots will be filtered out by
        // JavaScript if today is selected
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String minDate = today.format(dateFormatter);

        model.addAttribute("professionals", professionals);
        model.addAttribute("allSlots", allSlots);
        model.addAttribute("user", user);
        model.addAttribute("minDate", minDate);

        // Use layout template
        model.addAttribute("page", "appointments/book");
        model.addAttribute("title", "Book Appointment");
        model.addAttribute("activePage", "appointments");

        return "layout";
    }

    // Professional: View and manage schedule
    @GetMapping("/my-schedule")
    public String mySchedule(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Appointment> appointments;
        String pageTitle;
        String templatePage;

        // Check if user is professional or student
        if (user.getRole().toString().equals("PROFESSIONAL")) {
            appointments = appointmentService.getProfessionalAppointments(user);
            pageTitle = "My Schedule";
            templatePage = "appointments/professional-schedule";
        } else {
            appointments = appointmentService.getStudentAppointments(user);
            pageTitle = "My Appointments";
            templatePage = "appointments/schedule";
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("user", user);
        model.addAttribute("page", templatePage);
        model.addAttribute("title", pageTitle);

        return "layout";
    }

    // View rejected appointments (Students and Professionals)
    @GetMapping("/rejected")
    public String rejectedAppointments(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Appointment> rejectedAppointments;
        String pageTitle;
        String templatePage;

        // Check if user is professional or student
        if (user.getRole().toString().equals("PROFESSIONAL")) {
            rejectedAppointments = appointmentService.getProfessionalRejectedAppointments(user);
            pageTitle = "Rejected Appointments";
            templatePage = "appointments/professional-rejected";
        } else {
            rejectedAppointments = appointmentService.getStudentRejectedAppointments(user);
            pageTitle = "Rejected Appointments";
            templatePage = "appointments/rejected";
        }

        // Sort by most recent updated date first
        rejectedAppointments.sort((a, b) -> {
            if (a.getUpdatedAt() == null || b.getUpdatedAt() == null)
                return 0;
            return b.getUpdatedAt().compareTo(a.getUpdatedAt());
        });

        model.addAttribute("rejectedAppointments", rejectedAppointments);
        model.addAttribute("user", user);
        model.addAttribute("page", templatePage);
        model.addAttribute("title", pageTitle);

        return "layout";
    }

    // Save appointment - POST request from book form
    @PostMapping("/save")
    public String saveAppointment(@RequestParam Long professionalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
            @RequestParam String startTime,
            @RequestParam String endTime,
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

        try {
            LocalTime slotStart = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime slotEnd = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));

            appointmentService.createAppointment(user, professional, appointmentDate,
                    slotStart, slotEnd, notes);
        } catch (IllegalArgumentException e) {
            return "redirect:/appointments/book";
        }

        return "redirect:/appointments";
    }

    // AJAX: Get available time slots for a date and professional
    @GetMapping("/api/available-slots")
    @ResponseBody
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long professionalId,
            @RequestParam(required = false) Long studentId,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        User professional = userRepository.findById(professionalId).orElse(null);
        if (professional == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Professional not found"));
        }

        // If studentId not provided, use the current user
        if (studentId == null) {
            studentId = user.getId();
        }

        User student = userRepository.findById(studentId).orElse(null);
        if (student == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Student not found"));
        }

        // Get available slots for the professional
        List<TimeSlotUtil.TimeSlot> availableSlots = appointmentService.getAvailableTimeSlots(date, professional);

        // Filter out past time slots if the selected date is today
        LocalDate today = LocalDate.now();
        if (date.equals(today)) {
            LocalTime now = LocalTime.now();
            availableSlots = availableSlots.stream()
                    .filter(slot -> slot.getStartTime().isAfter(now))
                    .collect(Collectors.toList());
        }

        // Filter out slots where the student already has an appointment on this date
        List<TimeSlotUtil.TimeSlot> studentAppointments = appointmentService.getStudentAppointmentsForDate(student,
                date);

        availableSlots = availableSlots.stream()
                .filter(slot -> !hasConflict(slot, studentAppointments))
                .collect(Collectors.toList());

        // Filter out slots where the student has a STUDENT_PROPOSED suggestion (pending
        // counter-offer)
        List<Appointment> studentProposals = appointmentRepository.findByStudentAndStatus(student,
                AppointmentStatus.STUDENT_PROPOSED);
        for (Appointment proposal : studentProposals) {
            if (proposal.getSuggestedAppointmentDate() != null &&
                    proposal.getSuggestedTimeSlotStart() != null &&
                    proposal.getSuggestedTimeSlotEnd() != null &&
                    proposal.getSuggestedAppointmentDate().equals(date)) {
                LocalTime suggestedStart = proposal.getSuggestedTimeSlotStart();
                LocalTime suggestedEnd = proposal.getSuggestedTimeSlotEnd();
                availableSlots = availableSlots.stream()
                        .filter(slot -> {
                            // Check if slot overlaps with suggested time
                            boolean overlaps = slot.getStartTime().isBefore(suggestedEnd) &&
                                    slot.getEndTime().isAfter(suggestedStart);
                            return !overlaps;
                        })
                        .collect(Collectors.toList());
            }
        }

        List<Map<String, String>> slots = availableSlots.stream()
                .map(slot -> Map.of(
                        "start", slot.getStartTime().toString(),
                        "end", slot.getEndTime().toString(),
                        "display", slot.toString()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "slots", slots,
                "allSlots", TimeSlotUtil.generateAllTimeSlots().stream()
                        .map(slot -> Map.of(
                                "start", slot.getStartTime().toString(),
                                "end", slot.getEndTime().toString(),
                                "display", slot.toString()))
                        .collect(Collectors.toList())));
    }

    // Helper method to check if a slot conflicts with existing appointments
    private boolean hasConflict(TimeSlotUtil.TimeSlot slot, List<TimeSlotUtil.TimeSlot> existingSlots) {
        for (TimeSlotUtil.TimeSlot existing : existingSlots) {
            // Check if slots overlap (they can be adjacent - start of one can equal end of
            // other)
            // Conflict occurs only if: slot overlaps with existing time range
            // No conflict if: slot.end <= existing.start OR slot.start >= existing.end
            if (!(slot.getEndTime().compareTo(existing.getStartTime()) <= 0 ||
                    slot.getStartTime().compareTo(existing.getEndTime()) >= 0)) {
                return true;
            }
        }
        return false;
    }

    // AJAX: Validate continuous slots
    @PostMapping("/api/validate-slots")
    @ResponseBody
    public ResponseEntity<?> validateSlots(@RequestParam List<String> slots) {
        if (slots == null || slots.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", "No slots selected"));
        }

        try {
            List<TimeSlotUtil.TimeSlot> selectedSlots = slots.stream()
                    .map(slotStr -> {
                        String[] parts = slotStr.split("-");
                        if (parts.length != 2) {
                            throw new IllegalArgumentException("Invalid slot format: " + slotStr);
                        }
                        LocalTime start = LocalTime.parse(parts[0].trim());
                        LocalTime end = LocalTime.parse(parts[1].trim());
                        return new TimeSlotUtil.TimeSlot(start, end);
                    })
                    .collect(Collectors.toList());

            boolean isValid = appointmentService.validateContinuousSlots(selectedSlots);

            if (!isValid) {
                return ResponseEntity.ok(Map.of(
                        "valid", false,
                        "error", "Please select continuous time slots only. Slots must be consecutive with no gaps."));
            }

            LocalTime startTime = TimeSlotUtil.getEarliestStartTime(selectedSlots);
            LocalTime endTime = TimeSlotUtil.getLatestEndTime(selectedSlots);
            int durationMinutes = TimeSlotUtil.getTotalDurationMinutes(selectedSlots);

            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "startTime", startTime.toString(),
                    "endTime", endTime.toString(),
                    "duration", durationMinutes / 60.0 + " hours"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "error", "Error validating slots: " + e.getMessage()));
        }
    }

    // Professional: Approve appointment
    @PostMapping("/api/{id}/approve")
    @ResponseBody
    public ResponseEntity<?> approveAppointment(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        try {
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

            // Check if user is the professional for this appointment
            if (!appointment.getProfessional().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Unauthorized to approve this appointment"));
            }

            appointmentService.approveAppointment(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Appointment approved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Professional: Reject appointment
    @PostMapping("/api/{id}/reject")
    @ResponseBody
    public ResponseEntity<?> rejectAppointment(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        try {
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

            // Check if user is the professional for this appointment
            if (!appointment.getProfessional().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Unauthorized to reject this appointment"));
            }

            appointmentService.rejectAppointment(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Appointment rejected"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // AJAX: Get current user
    @GetMapping("/api/current-user")
    @ResponseBody
    public Map<String, Object> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Map.of("id", 0L, "role", "GUEST");
        }

        return Map.ofEntries(
                Map.entry("id", user.getId()),
                Map.entry("name", user.getName()),
                Map.entry("email", user.getEmail()),
                Map.entry("role", user.getRole().toString()));
    }

    // AJAX: Get appointment by ID
    @GetMapping("/{appointmentId}")
    @ResponseBody
    public ResponseEntity<?> getAppointmentById(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment != null) {
            return ResponseEntity.ok(Map.of(
                    "id", appointment.getId(),
                    "meetingLink", appointment.getMeetingLink() != null ? appointment.getMeetingLink() : ""));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // AJAX: Get appointment by report ID
    @GetMapping("/api/by-report/{reportId}")
    @ResponseBody
    public ResponseEntity<?> getAppointmentByReportId(@PathVariable Long reportId) {
        System.out.println("DEBUG: Fetching appointment for reportId: " + reportId);
        Appointment appointment = appointmentRepository.findByReportId(reportId);
        if (appointment != null) {
            System.out.println("DEBUG: Appointment found! ID: " + appointment.getId());
            return ResponseEntity.ok(appointment);
        } else {
            System.out.println("DEBUG: No appointment found for reportId: " + reportId);
            return ResponseEntity.notFound().build();
        }
    }

    // AJAX: Get user's appointments
    @GetMapping("/api/my-appointments")
    @ResponseBody
    public List<Map<String, Object>> myAppointmentsApi(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return List.of();
        }

        List<Appointment> appointments = appointmentService.getStudentAppointments(user);

        return appointments.stream().map(apt -> Map.ofEntries(
                Map.entry("id", apt.getId()),
                Map.entry("date", apt.getAppointmentDate().toString()),
                Map.entry("startTime", apt.getTimeSlotStart().toString()),
                Map.entry("endTime", apt.getTimeSlotEnd().toString()),
                Map.entry("status", apt.getStatus().toString()),
                Map.entry("notes", apt.getNotes() != null ? apt.getNotes() : ""),
                Map.entry("professional", Map.ofEntries(
                        Map.entry("id", apt.getProfessional().getId()),
                        Map.entry("name", apt.getProfessional().getName())))))
                .toList();
    }

    // Delete appointment
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

    // Professional: Book appointment for a student (from report detail page)
    @PostMapping("/book")
    public String bookAppointmentForStudent(
            @RequestParam Long studentId,
            @RequestParam Long reportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
            @RequestParam String appointmentTime,
            @RequestParam(required = false) Integer appointmentDuration,
            @RequestParam(required = false) String notes,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        User student = userRepository.findById(studentId).orElse(null);
        if (student == null) {
            return "redirect:/professional/reports";
        }

        try {
            // Parse the start time
            LocalTime slotStart = LocalTime.parse(appointmentTime, DateTimeFormatter.ofPattern("HH:mm"));

            // Calculate end time based on duration (each slot is 30 minutes, duration is
            // number of slots)
            int durationMinutes = (appointmentDuration != null && appointmentDuration > 0) ? appointmentDuration * 30
                    : 30;
            LocalTime slotEnd = slotStart.plusMinutes(durationMinutes);

            // Check if there's already a STUDENT_PROPOSED appointment with this reportId
            // If so, update it instead of creating a new one
            Appointment existingProposal = appointmentRepository.findByReportIdAndStatus(reportId,
                    AppointmentStatus.STUDENT_PROPOSED);

            Appointment appointment;
            if (existingProposal != null) {
                // Update the existing student proposal with professional's response times
                // Professional is responding to student's counter-proposal
                appointment = existingProposal;
                appointment.setAppointmentDate(appointmentDate);
                appointment.setTimeSlotStart(slotStart);
                appointment.setTimeSlotEnd(slotEnd);
                appointment.setStatus(AppointmentStatus.PENDING);
                // Clear the suggested times since we're now using the main appointment fields
                appointment.setSuggestedAppointmentDate(null);
                appointment.setSuggestedTimeSlotStart(null);
                appointment.setSuggestedTimeSlotEnd(null);
                if (notes != null) {
                    appointment.setNotes(notes);
                }
                appointmentRepository.save(appointment);
            } else {
                // Create appointment with PENDING status when professional proposes from report
                // Student will then approve or reject with suggestion
                appointment = appointmentService.createAppointment(student, user, appointmentDate,
                        slotStart, slotEnd, notes, AppointmentStatus.PENDING);

                // Set the report ID to link to this report
                if (reportId != null && reportId > 0) {
                    appointment.setReportId(reportId);
                    appointmentRepository.save(appointment);
                }
            }

            // Update report status to 'scheduled' via API
            if (reportId != null && reportId > 0) {
                // This will be done via the response handling
            }

        } catch (IllegalArgumentException e) {
            return "redirect:/professional/reports/" + reportId;
        }

        return "redirect:/professional/reports/" + reportId;
    }

    // ==================== Student Appointment Actions ====================

    /**
     * Student approves a pending appointment from professional
     */
    @PostMapping("/{appointmentId}/student-approve")
    @ResponseBody
    public ResponseEntity<?> studentApproveAppointment(
            @PathVariable Long appointmentId,
            HttpSession session) {
        try {
            User student = (User) session.getAttribute("user");
            if (student == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }

            appointmentService.studentApproveAppointment(appointmentId, student);
            return ResponseEntity.ok(Map.of("success", true, "message", "Appointment approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Student rejects appointment and proposes alternative date/time
     */
    @PostMapping("/{appointmentId}/student-reject-suggest")
    @ResponseBody
    public ResponseEntity<?> studentRejectAndProposeSuggestion(
            @PathVariable Long appointmentId,
            @RequestParam(required = false) String suggestedDate,
            @RequestParam(required = false) String suggestedStartTime,
            @RequestParam(required = false) String suggestedEndTime,
            HttpSession session) {
        try {
            User student = (User) session.getAttribute("user");
            if (student == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }

            LocalDate date = LocalDate.parse(suggestedDate);
            LocalTime startTime = LocalTime.parse(suggestedStartTime);
            LocalTime endTime = LocalTime.parse(suggestedEndTime);

            appointmentService.studentRejectAndProposeSuggestion(appointmentId, student, date, startTime, endTime);
            return ResponseEntity.ok(Map.of("success", true, "message", "Suggestion submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get available time slots for a professional on a given date
     * Used by student when suggesting alternative times
     */
    @GetMapping("/{appointmentId}/available-slots")
    @ResponseBody
    public ResponseEntity<?> getAvailableSlots(
            @PathVariable Long appointmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session) {
        try {
            User student = (User) session.getAttribute("user");
            if (student == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }

            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new Exception("Appointment not found"));

            if (!appointment.getStudent().getId().equals(student.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Unauthorized"));
            }

            List<TimeSlotUtil.TimeSlot> availableSlots = appointmentService.getAvailableTimeSlots(date,
                    appointment.getProfessional());

            // Filter out past time slots if the selected date is today
            LocalDate today = LocalDate.now();
            if (date.equals(today)) {
                LocalTime now = LocalTime.now();
                availableSlots = availableSlots.stream()
                        .filter(slot -> slot.getStartTime().isAfter(now))
                        .collect(Collectors.toList());
            }

            // Filter out slots where the professional has other appointments
            List<Appointment> professionalAppointments = appointmentRepository
                    .findByAppointmentDateAndProfessionalAndStatus(
                            date, appointment.getProfessional(), AppointmentStatus.APPROVED);
            List<Appointment> pendingAppointments = appointmentRepository.findByAppointmentDateAndProfessionalAndStatus(
                    date, appointment.getProfessional(), AppointmentStatus.PENDING);

            List<Appointment> allConflicts = new ArrayList<>(professionalAppointments);
            allConflicts.addAll(pendingAppointments);

            for (Appointment conflict : allConflicts) {
                // Skip the current appointment being evaluated
                if (!conflict.getId().equals(appointmentId)) {
                    LocalTime conflictStart = conflict.getTimeSlotStart();
                    LocalTime conflictEnd = conflict.getTimeSlotEnd();
                    availableSlots = availableSlots.stream()
                            .filter(slot -> {
                                // Check if slot overlaps with conflict
                                boolean overlaps = slot.getStartTime().isBefore(conflictEnd) &&
                                        slot.getEndTime().isAfter(conflictStart);
                                return !overlaps;
                            })
                            .collect(Collectors.toList());
                }
            }

            // Filter out professional's suggested times (when they propose a different
            // appointment)
            // If the professional has suggested times for this appointment, student cannot
            // select those
            if (appointment.getSuggestedTimeSlotStart() != null && appointment.getSuggestedTimeSlotEnd() != null) {
                LocalTime suggestedStart = appointment.getSuggestedTimeSlotStart();
                LocalTime suggestedEnd = appointment.getSuggestedTimeSlotEnd();
                availableSlots = availableSlots.stream()
                        .filter(slot -> {
                            // Check if slot overlaps with professional's suggested times
                            boolean overlaps = slot.getStartTime().isBefore(suggestedEnd) &&
                                    slot.getEndTime().isAfter(suggestedStart);
                            return !overlaps;
                        })
                        .collect(Collectors.toList());
            }

            // Also filter out student's own suggested times from OTHER appointments with
            // the same professional
            List<Appointment> studentOtherSuggestions = appointmentRepository.findByStudentAndStatus(student,
                    AppointmentStatus.STUDENT_PROPOSED);
            for (Appointment otherSuggestion : studentOtherSuggestions) {
                // Skip the current appointment being evaluated
                if (!otherSuggestion.getId().equals(appointmentId)) {
                    if (otherSuggestion.getSuggestedAppointmentDate() != null &&
                            otherSuggestion.getSuggestedAppointmentDate().equals(date) &&
                            otherSuggestion.getSuggestedTimeSlotStart() != null &&
                            otherSuggestion.getSuggestedTimeSlotEnd() != null) {

                        LocalTime otherSuggestedStart = otherSuggestion.getSuggestedTimeSlotStart();
                        LocalTime otherSuggestedEnd = otherSuggestion.getSuggestedTimeSlotEnd();
                        availableSlots = availableSlots.stream()
                                .filter(slot -> {
                                    // Check if slot overlaps with other appointment's suggested times
                                    boolean overlaps = slot.getStartTime().isBefore(otherSuggestedEnd) &&
                                            slot.getEndTime().isAfter(otherSuggestedStart);
                                    return !overlaps;
                                })
                                .collect(Collectors.toList());
                    }
                }
            }

            // Convert to JSON-friendly format
            var slotsList = availableSlots.stream()
                    .map(slot -> Map.of(
                            "startTime", slot.getStartTime().toString(),
                            "endTime", slot.getEndTime().toString()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(slotsList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Professional schedules appointment from student's suggestion
     */
    @PostMapping("/{appointmentId}/professional-schedule-suggestion")
    @ResponseBody
    public ResponseEntity<?> professionalScheduleFromSuggestion(
            @PathVariable Long appointmentId,
            HttpSession session) {
        try {
            User professional = (User) session.getAttribute("user");
            if (professional == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }

            appointmentService.professionalScheduleFromSuggestion(appointmentId, professional);
            return ResponseEntity
                    .ok(Map.of("success", true, "message", "Appointment scheduled from student suggestion"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Professional assigns meeting link to appointment
     */
    @PostMapping("/{appointmentId}/meeting-link")
    @ResponseBody
    public ResponseEntity<?> assignMeetingLink(
            @PathVariable Long appointmentId,
            @org.springframework.web.bind.annotation.RequestBody Map<String, String> request,
            HttpSession session) {
        try {
            User professional = (User) session.getAttribute("user");
            if (professional == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }

            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            // Verify professional owns this appointment
            if (!appointment.getProfessional().getId().equals(professional.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not authorized"));
            }

            String meetingLink = request.get("meetingLink");
            if (meetingLink == null || meetingLink.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Meeting link cannot be empty"));
            }

            appointment.setMeetingLink(meetingLink);
            appointmentRepository.save(appointment);

            return ResponseEntity.ok(Map.of("success", true, "message", "Meeting link saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}