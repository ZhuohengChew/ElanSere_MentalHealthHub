package com.mentalhealthhub.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.AppointmentStatus;
import com.mentalhealthhub.model.Report;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.AppointmentRepository;
import com.mentalhealthhub.repository.ReportRepository;
import com.mentalhealthhub.util.TimeSlotUtil;

@Service
public class AppointmentService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final AppointmentRepository appointmentRepository;
    private final ReportRepository reportRepository;

    /**
     * Constructor-based dependency injection.
     * Spring IoC container will automatically inject the required dependencies
     * when creating an instance of AppointmentService.
     * 
     * @param appointmentRepository Repository for appointment data access
     * @param reportRepository      Repository for report data access
     */
    public AppointmentService(AppointmentRepository appointmentRepository, ReportRepository reportRepository) {
        this.appointmentRepository = appointmentRepository;
        this.reportRepository = reportRepository;
    }

    /**
     * Gets all available time slots for a specific date and professional
     * Excludes slots that are already booked (PENDING or APPROVED status)
     */
    public List<TimeSlotUtil.TimeSlot> getAvailableTimeSlots(LocalDate date, User professional) {
        List<TimeSlotUtil.TimeSlot> allSlots = TimeSlotUtil.generateAllTimeSlots();

        // Use professional ID instead of object to avoid JPA matching issues
        Long professionalId = professional.getId();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔍 [DEBUG] getAvailableTimeSlots START");
        System.out.println("=".repeat(80));
        System.out.println("   Date: " + date);
        System.out.println("   Professional ID: " + professionalId);
        System.out.println("   Professional Name: " + professional.getName());
        System.out.println("   All slots available: " + allSlots.size());

        // Fetch occupied appointments by querying for PENDING and APPROVED separately
        System.out.println("\n   📋 Querying for PENDING appointments...");
        List<Appointment> pendingAppointments = appointmentRepository.findByAppointmentDateAndProfessionalIdAndStatus(
                date, professionalId, AppointmentStatus.PENDING);
        System.out.println("      PENDING found: " + pendingAppointments.size());
        for (Appointment a : pendingAppointments) {
            System.out.println(
                    "      - " + a.getTimeSlotStart() + " to " + a.getTimeSlotEnd() + " (ID: " + a.getId() + ")");
        }

        System.out.println("\n   📋 Querying for APPROVED appointments...");
        List<Appointment> approvedAppointments = appointmentRepository.findByAppointmentDateAndProfessionalIdAndStatus(
                date, professionalId, AppointmentStatus.APPROVED);
        System.out.println("      APPROVED found: " + approvedAppointments.size());
        for (Appointment a : approvedAppointments) {
            System.out.println(
                    "      - " + a.getTimeSlotStart() + " to " + a.getTimeSlotEnd() + " (ID: " + a.getId() + ")");
        }

        // Combine both lists
        List<Appointment> occupiedAppointments = new ArrayList<>();
        occupiedAppointments.addAll(pendingAppointments);
        occupiedAppointments.addAll(approvedAppointments);

        System.out.println("\n   ✅ Total occupied appointments: " + occupiedAppointments.size());

        // Filter slots using time overlap checking
        List<TimeSlotUtil.TimeSlot> availableSlots = new ArrayList<>();
        System.out.println("\n   🎯 Filtering " + allSlots.size() + " total slots...");
        int filteredCount = 0;
        for (TimeSlotUtil.TimeSlot slot : allSlots) {
            boolean isOccupied = false;
            for (Appointment a : occupiedAppointments) {
                // Check if slot overlaps with appointment time
                // Overlap occurs if: slot.start < appointment.end AND slot.end >
                // appointment.start
                if (slot.getStartTime().isBefore(a.getTimeSlotEnd()) &&
                        slot.getEndTime().isAfter(a.getTimeSlotStart())) {
                    isOccupied = true;
                    System.out.println("      BLOCKED: " + TIME_FORMATTER.format(slot.getStartTime()) + "-"
                            + TIME_FORMATTER.format(slot.getEndTime()) +
                            " overlaps with appointment " + TIME_FORMATTER.format(a.getTimeSlotStart()) + "-"
                            + TIME_FORMATTER.format(a.getTimeSlotEnd()));
                    filteredCount++;
                    break;
                }
            }
            if (!isOccupied) {
                availableSlots.add(slot);
            }
        }

        System.out.println("\n   📊 Results:");
        System.out.println("      Slots filtered out: " + filteredCount);
        System.out.println("      Available slots: " + availableSlots.size());
        System.out.println("=".repeat(80) + "\n");
        return availableSlots;
    }

    /**
     * Checks if a time slot is available (not booked by PENDING or APPROVED
     * appointments)
     */
    public boolean isTimeSlotAvailable(LocalDate date, User professional, LocalTime slotStart, LocalTime slotEnd) {
        List<Appointment> conflictingAppointments = appointmentRepository
                .findConflictingAppointments(date, professional, slotStart, slotEnd);
        return conflictingAppointments.isEmpty();
    }

    /**
     * Validates that selected time slots are continuous (no gaps)
     */
    public boolean validateContinuousSlots(List<TimeSlotUtil.TimeSlot> selectedSlots) {
        if (!TimeSlotUtil.areSlotsConsecutive(selectedSlots)) {
            return false;
        }
        if (!TimeSlotUtil.slotsValidateLunchBreak(selectedSlots)) {
            return false;
        }
        return true;
    }

    /**
     * Creates a new appointment with the given details
     */
    public Appointment createAppointment(User student, User professional, LocalDate date,
            LocalTime startTime, LocalTime endTime, String notes) {
        return createAppointment(student, professional, date, startTime, endTime, notes, AppointmentStatus.PENDING);
    }

    /**
     * Creates an appointment with a specific status
     */
    public Appointment createAppointment(User student, User professional, LocalDate date,
            LocalTime startTime, LocalTime endTime, String notes, AppointmentStatus status) {
        // Validate continuous slots
        LocalTime currentTime = startTime;
        List<TimeSlotUtil.TimeSlot> slots = new ArrayList<>();
        while (currentTime.isBefore(endTime)) {
            LocalTime slotEnd = currentTime.plusMinutes(TimeSlotUtil.SLOT_DURATION_MINUTES);
            slots.add(new TimeSlotUtil.TimeSlot(currentTime, slotEnd));
            currentTime = slotEnd;
        }

        if (!validateContinuousSlots(slots)) {
            throw new IllegalArgumentException("Selected time slots are not continuous");
        }

        // Check for conflicts
        if (!isTimeSlotAvailable(date, professional, startTime, endTime)) {
            throw new IllegalArgumentException("Selected time slot is not available");
        }

        Appointment appointment = Appointment.builder()
                .student(student)
                .professional(professional)
                .appointmentDate(date)
                .timeSlotStart(startTime)
                .timeSlotEnd(endTime)
                .status(status)
                .notes(notes)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return appointmentRepository.save(appointment);
    }

    /**
     * Approves a pending appointment
     */
    public Appointment approveAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalArgumentException("Only pending appointments can be approved");
        }

        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    /**
     * Rejects a pending appointment
     */
    public Appointment rejectAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalArgumentException("Only pending appointments can be rejected");
        }

        appointment.setStatus(AppointmentStatus.REJECTED);
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    /**
     * Gets all pending and approved appointments for a student
     */
    public List<Appointment> getStudentAppointments(User student) {
        List<Appointment> pending = appointmentRepository.findByStudentAndStatus(student, AppointmentStatus.PENDING);
        List<Appointment> approved = appointmentRepository.findByStudentAndStatus(student, AppointmentStatus.APPROVED);

        pending.addAll(approved);
        return pending.stream()
                .sorted((a1, a2) -> a1.getAppointmentDate().compareTo(a2.getAppointmentDate()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all rejected appointments for a student
     */
    public List<Appointment> getStudentRejectedAppointments(User student) {
        return appointmentRepository.findByStudentAndStatusOrderByAppointmentDateDesc(student,
                AppointmentStatus.REJECTED);
    }

    /**
     * Gets all pending and approved appointments for a professional
     */
    public List<Appointment> getProfessionalAppointments(User professional) {
        List<Appointment> pending = appointmentRepository.findByProfessionalAndStatus(professional,
                AppointmentStatus.PENDING);
        List<Appointment> approved = appointmentRepository.findByProfessionalAndStatus(professional,
                AppointmentStatus.APPROVED);

        pending.addAll(approved);
        return pending.stream()
                .sorted((a1, a2) -> a1.getAppointmentDate().compareTo(a2.getAppointmentDate()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all pending appointments for a professional (for review)
     */
    public List<Appointment> getProfessionalPendingAppointments(User professional) {
        return appointmentRepository.findByProfessionalAndStatusOrderByAppointmentDateDesc(professional,
                AppointmentStatus.PENDING);
    }

    /**
     * Gets all rejected appointments for a professional
     */
    public List<Appointment> getProfessionalRejectedAppointments(User professional) {
        return appointmentRepository.findByProfessionalAndStatusOrderByAppointmentDateDesc(professional,
                AppointmentStatus.REJECTED);
    }

    /**
     * Gets student's appointments on a specific date (both PENDING and APPROVED)
     * Used to filter out occupied time slots
     */
    public List<TimeSlotUtil.TimeSlot> getStudentAppointmentsForDate(User student, LocalDate date) {
        List<Appointment> pendingAppointments = appointmentRepository.findByAppointmentDateAndStudentAndStatus(
                date, student, AppointmentStatus.PENDING);
        List<Appointment> approvedAppointments = appointmentRepository.findByAppointmentDateAndStudentAndStatus(
                date, student, AppointmentStatus.APPROVED);

        List<TimeSlotUtil.TimeSlot> studentSlots = new ArrayList<>();

        // Convert appointments to time slots
        pendingAppointments.forEach(
                apt -> studentSlots.add(new TimeSlotUtil.TimeSlot(apt.getTimeSlotStart(), apt.getTimeSlotEnd())));
        approvedAppointments.forEach(
                apt -> studentSlots.add(new TimeSlotUtil.TimeSlot(apt.getTimeSlotStart(), apt.getTimeSlotEnd())));

        return studentSlots;
    }

    // ==================== Student Appointment Actions ====================
    /**
     * Student approves a pending appointment from professional
     * Only available when status is PENDING and reportId is not null
     * (professional-created)
     */
    public void studentApproveAppointment(Long appointmentId, User student) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify student owns this appointment
        if (!appointment.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Unauthorized: Student can only approve their own appointments");
        }

        // Can only approve PENDING appointments from professional
        if (appointment.getStatus() != AppointmentStatus.PENDING || appointment.getReportId() == null) {
            throw new RuntimeException("Can only approve pending appointments from professional");
        }

        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
    }

    /**
     * Student rejects pending appointment and proposes alternative date/time
     * Only available when status is PENDING and reportId is not null
     * (professional-created)
     */
    public void studentRejectAndProposeSuggestion(Long appointmentId, User student,
            LocalDate suggestedDate, LocalTime suggestedStart, LocalTime suggestedEnd) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify student owns this appointment
        if (!appointment.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Unauthorized: Student can only reject their own appointments");
        }

        // Can only reject PENDING appointments from professional
        if (appointment.getStatus() != AppointmentStatus.PENDING || appointment.getReportId() == null) {
            throw new RuntimeException("Can only reject pending appointments from professional");
        }

        // Validate suggested timeslot is available for the professional
        List<Appointment> conflicts = appointmentRepository.findByAppointmentDateAndProfessionalIdAndStatus(
                suggestedDate,
                appointment.getProfessional().getId(),
                AppointmentStatus.PENDING);

        List<Appointment> approvedConflicts = appointmentRepository.findByAppointmentDateAndProfessionalIdAndStatus(
                suggestedDate,
                appointment.getProfessional().getId(),
                AppointmentStatus.APPROVED);

        conflicts.addAll(approvedConflicts);

        // Check for overlaps (exclude self if updating same appointment)
        for (Appointment conflict : conflicts) {
            if (!conflict.getId().equals(appointmentId)) {
                if (timeOverlaps(suggestedStart, suggestedEnd, conflict.getTimeSlotStart(),
                        conflict.getTimeSlotEnd())) {
                    throw new RuntimeException("Suggested time slot is not available for the professional");
                }
            }
        }

        // Set suggested fields
        appointment.setSuggestedAppointmentDate(suggestedDate);
        appointment.setSuggestedTimeSlotStart(suggestedStart);
        appointment.setSuggestedTimeSlotEnd(suggestedEnd);

        // Change status to indicate student has proposed alternative
        appointment.setStatus(AppointmentStatus.STUDENT_PROPOSED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        // Update report status back to "reviewed" (no longer scheduled)
        if (appointment.getReportId() != null) {
            Report report = reportRepository.findById(appointment.getReportId()).orElse(null);
            if (report != null) {
                report.setStatus("reviewed");
                reportRepository.save(report);
            }
        }
    }

    /**
     * Professional schedules appointment from student's suggestion
     * Moves suggested date/time to actual appointment date/time
     * Sets status back to PENDING for student to respond again
     */
    public void professionalScheduleFromSuggestion(Long appointmentId, User professional) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify professional owns this appointment
        if (!appointment.getProfessional().getId().equals(professional.getId())) {
            throw new RuntimeException("Unauthorized: Professional can only schedule their own appointments");
        }

        // Can only schedule from STUDENT_PROPOSED status
        if (appointment.getStatus() != AppointmentStatus.STUDENT_PROPOSED) {
            throw new RuntimeException("Can only schedule from student proposed appointments");
        }

        // Verify suggestion exists
        if (appointment.getSuggestedAppointmentDate() == null ||
                appointment.getSuggestedTimeSlotStart() == null ||
                appointment.getSuggestedTimeSlotEnd() == null) {
            throw new RuntimeException("No suggestion found for this appointment");
        }

        // Validate suggested slot is still available
        List<Appointment> conflicts = appointmentRepository.findByAppointmentDateAndProfessionalIdAndStatus(
                appointment.getSuggestedAppointmentDate(),
                professional.getId(),
                AppointmentStatus.PENDING);

        List<Appointment> approvedConflicts = appointmentRepository.findByAppointmentDateAndProfessionalIdAndStatus(
                appointment.getSuggestedAppointmentDate(),
                professional.getId(),
                AppointmentStatus.APPROVED);

        conflicts.addAll(approvedConflicts);

        for (Appointment conflict : conflicts) {
            if (!conflict.getId().equals(appointmentId)) {
                if (timeOverlaps(appointment.getSuggestedTimeSlotStart(), appointment.getSuggestedTimeSlotEnd(),
                        conflict.getTimeSlotStart(), conflict.getTimeSlotEnd())) {
                    throw new RuntimeException(
                            "Suggested time slot is no longer available. Please reject and propose a different time.");
                }
            }
        }

        // Move suggested → actual
        appointment.setAppointmentDate(appointment.getSuggestedAppointmentDate());
        appointment.setTimeSlotStart(appointment.getSuggestedTimeSlotStart());
        appointment.setTimeSlotEnd(appointment.getSuggestedTimeSlotEnd());

        // Clear suggested fields
        appointment.setSuggestedAppointmentDate(null);
        appointment.setSuggestedTimeSlotStart(null);
        appointment.setSuggestedTimeSlotEnd(null);

        // Set to APPROVED when professional accepts the suggestion
        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        // Update report status back to "scheduled"
        if (appointment.getReportId() != null) {
            Report report = reportRepository.findById(appointment.getReportId()).orElse(null);
            if (report != null) {
                report.setStatus("scheduled");
                reportRepository.save(report);
            }
        }
    }

    /**
     * Helper method to check if two time slots overlap
     */
    private boolean timeOverlaps(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
