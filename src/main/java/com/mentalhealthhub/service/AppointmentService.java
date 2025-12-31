package com.mentalhealthhub.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.AppointmentStatus;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.AppointmentRepository;
import com.mentalhealthhub.util.TimeSlotUtil;

@Service
public class AppointmentService {
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
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
            System.out.println("      - " + a.getTimeSlotStart() + " to " + a.getTimeSlotEnd() + " (ID: " + a.getId() + ")");
        }
        
        System.out.println("\n   📋 Querying for APPROVED appointments...");
        List<Appointment> approvedAppointments = appointmentRepository.findByAppointmentDateAndProfessionalIdAndStatus(
            date, professionalId, AppointmentStatus.APPROVED);
        System.out.println("      APPROVED found: " + approvedAppointments.size());
        for (Appointment a : approvedAppointments) {
            System.out.println("      - " + a.getTimeSlotStart() + " to " + a.getTimeSlotEnd() + " (ID: " + a.getId() + ")");
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
                // Overlap occurs if: slot.start < appointment.end AND slot.end > appointment.start
                if (slot.getStartTime().isBefore(a.getTimeSlotEnd()) && 
                    slot.getEndTime().isAfter(a.getTimeSlotStart())) {
                    isOccupied = true;
                    System.out.println("      BLOCKED: " + TIME_FORMATTER.format(slot.getStartTime()) + "-" + TIME_FORMATTER.format(slot.getEndTime()) + 
                        " overlaps with appointment " + TIME_FORMATTER.format(a.getTimeSlotStart()) + "-" + TIME_FORMATTER.format(a.getTimeSlotEnd()));
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
     * Checks if a time slot is available (not booked by PENDING or APPROVED appointments)
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
        return appointmentRepository.findByStudentAndStatusOrderByAppointmentDateDesc(student, AppointmentStatus.REJECTED);
    }
    
    /**
     * Gets all pending and approved appointments for a professional
     */
    public List<Appointment> getProfessionalAppointments(User professional) {
        List<Appointment> pending = appointmentRepository.findByProfessionalAndStatus(professional, AppointmentStatus.PENDING);
        List<Appointment> approved = appointmentRepository.findByProfessionalAndStatus(professional, AppointmentStatus.APPROVED);
        
        pending.addAll(approved);
        return pending.stream()
            .sorted((a1, a2) -> a1.getAppointmentDate().compareTo(a2.getAppointmentDate()))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all pending appointments for a professional (for review)
     */
    public List<Appointment> getProfessionalPendingAppointments(User professional) {
        return appointmentRepository.findByProfessionalAndStatusOrderByAppointmentDateDesc(professional, AppointmentStatus.PENDING);
    }
    
    /**
     * Gets all rejected appointments for a professional
     */
    public List<Appointment> getProfessionalRejectedAppointments(User professional) {
        return appointmentRepository.findByProfessionalAndStatusOrderByAppointmentDateDesc(professional, AppointmentStatus.REJECTED);
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
        pendingAppointments.forEach(apt -> 
            studentSlots.add(new TimeSlotUtil.TimeSlot(apt.getTimeSlotStart(), apt.getTimeSlotEnd()))
        );
        approvedAppointments.forEach(apt -> 
            studentSlots.add(new TimeSlotUtil.TimeSlot(apt.getTimeSlotStart(), apt.getTimeSlotEnd()))
        );
        
        return studentSlots;
    }
}
