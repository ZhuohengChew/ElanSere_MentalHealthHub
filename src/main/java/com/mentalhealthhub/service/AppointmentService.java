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

    public AppointmentService(AppointmentRepository appointmentRepository, ReportRepository reportRepository) {
        this.appointmentRepository = appointmentRepository;
        this.reportRepository = reportRepository;
    }

    public List<TimeSlotUtil.TimeSlot> getAvailableTimeSlots(LocalDate date, User professional) {
        List<TimeSlotUtil.TimeSlot> allSlots = TimeSlotUtil.generateAllTimeSlots();
        Long professionalId = professional.getId();
        List<Appointment> pendingAppointments = appointmentRepository.findByAppointmentDateAndProfessionalIdAndStatus(
                date, professionalId, AppointmentStatus.PENDING);
        List<Appointment> approvedAppointments = appointmentRepository.findByAppointmentDateAndProfessionalIdAndStatus(
                date, professionalId, AppointmentStatus.APPROVED);
        List<Appointment> occupiedAppointments = new ArrayList<>();
        occupiedAppointments.addAll(pendingAppointments);
        occupiedAppointments.addAll(approvedAppointments);
        List<TimeSlotUtil.TimeSlot> availableSlots = new ArrayList<>();
        for (TimeSlotUtil.TimeSlot slot : allSlots) {
            boolean isOccupied = false;
            for (Appointment a : occupiedAppointments) {
                if (slot.getStartTime().isBefore(a.getTimeSlotEnd()) &&
                        slot.getEndTime().isAfter(a.getTimeSlotStart())) {
                    isOccupied = true;
                    break;
                }
            }
            if (!isOccupied) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }

    public boolean isTimeSlotAvailable(LocalDate date, User professional, LocalTime slotStart, LocalTime slotEnd) {
        List<Appointment> conflictingAppointments = appointmentRepository
                .findConflictingAppointments(date, professional, slotStart, slotEnd);
        return conflictingAppointments.isEmpty();
    }

    public boolean validateContinuousSlots(List<TimeSlotUtil.TimeSlot> selectedSlots) {
        if (!TimeSlotUtil.areSlotsConsecutive(selectedSlots)) {
            return false;
        }
        if (!TimeSlotUtil.slotsValidateLunchBreak(selectedSlots)) {
            return false;
        }
        return true;
    }

    public Appointment createAppointment(User student, User professional, LocalDate date,
            LocalTime startTime, LocalTime endTime, String notes) {
        return createAppointment(student, professional, date, startTime, endTime, notes, AppointmentStatus.PENDING);
    }

    public Appointment createAppointment(User student, User professional, LocalDate date,
            LocalTime startTime, LocalTime endTime, String notes, AppointmentStatus status) {
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

    public List<Appointment> getStudentAppointments(User student) {
        List<Appointment> pending = appointmentRepository.findByStudentAndStatus(student, AppointmentStatus.PENDING);
        List<Appointment> approved = appointmentRepository.findByStudentAndStatus(student, AppointmentStatus.APPROVED);

        pending.addAll(approved);
        return pending.stream()
                .sorted((a1, a2) -> a1.getAppointmentDate().compareTo(a2.getAppointmentDate()))
                .collect(Collectors.toList());
    }

    public List<Appointment> getStudentRejectedAppointments(User student) {
        return appointmentRepository.findByStudentAndStatusOrderByAppointmentDateDesc(student,
                AppointmentStatus.REJECTED);
    }

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

    public List<Appointment> getProfessionalPendingAppointments(User professional) {
        return appointmentRepository.findByProfessionalAndStatusOrderByAppointmentDateDesc(professional,
                AppointmentStatus.PENDING);
    }

    public List<Appointment> getProfessionalRejectedAppointments(User professional) {
        return appointmentRepository.findByProfessionalAndStatusOrderByAppointmentDateDesc(professional,
                AppointmentStatus.REJECTED);
    }

    public List<TimeSlotUtil.TimeSlot> getStudentAppointmentsForDate(User student, LocalDate date) {
        List<Appointment> pendingAppointments = appointmentRepository.findByAppointmentDateAndStudentAndStatus(
                date, student, AppointmentStatus.PENDING);
        List<Appointment> approvedAppointments = appointmentRepository.findByAppointmentDateAndStudentAndStatus(
                date, student, AppointmentStatus.APPROVED);

        List<TimeSlotUtil.TimeSlot> studentSlots = new ArrayList<>();

        pendingAppointments.forEach(
                apt -> studentSlots.add(new TimeSlotUtil.TimeSlot(apt.getTimeSlotStart(), apt.getTimeSlotEnd())));
        approvedAppointments.forEach(
                apt -> studentSlots.add(new TimeSlotUtil.TimeSlot(apt.getTimeSlotStart(), apt.getTimeSlotEnd())));

        return studentSlots;
    }

    public void studentApproveAppointment(Long appointmentId, User student) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Unauthorized: Student can only approve their own appointments");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING || appointment.getReportId() == null) {
            throw new RuntimeException("Can only approve pending appointments from professional");
        }

        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
    }

    public void studentRejectAndProposeSuggestion(Long appointmentId, User student,
            LocalDate suggestedDate, LocalTime suggestedStart, LocalTime suggestedEnd) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Unauthorized: Student can only reject their own appointments");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING || appointment.getReportId() == null) {
            throw new RuntimeException("Can only reject pending appointments from professional");
        }

        List<Appointment> conflicts = appointmentRepository.findByAppointmentDateAndProfessionalIdAndStatus(
                suggestedDate,
                appointment.getProfessional().getId(),
                AppointmentStatus.PENDING);

        List<Appointment> approvedConflicts = appointmentRepository.findByAppointmentDateAndProfessionalIdAndStatus(
                suggestedDate,
                appointment.getProfessional().getId(),
                AppointmentStatus.APPROVED);

        conflicts.addAll(approvedConflicts);

        for (Appointment conflict : conflicts) {
            if (!conflict.getId().equals(appointmentId)) {
                if (timeOverlaps(suggestedStart, suggestedEnd, conflict.getTimeSlotStart(),
                        conflict.getTimeSlotEnd())) {
                    throw new RuntimeException("Suggested time slot is not available for the professional");
                }
            }
        }

        appointment.setSuggestedAppointmentDate(suggestedDate);
        appointment.setSuggestedTimeSlotStart(suggestedStart);
        appointment.setSuggestedTimeSlotEnd(suggestedEnd);

        appointment.setStatus(AppointmentStatus.STUDENT_PROPOSED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        if (appointment.getReportId() != null) {
            Report report = reportRepository.findById(appointment.getReportId()).orElse(null);
            if (report != null) {
                report.setStatus("reviewed");
                reportRepository.save(report);
            }
        }
    }

    public void professionalScheduleFromSuggestion(Long appointmentId, User professional) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getProfessional().getId().equals(professional.getId())) {
            throw new RuntimeException("Unauthorized: Professional can only schedule their own appointments");
        }

        if (appointment.getStatus() != AppointmentStatus.STUDENT_PROPOSED) {
            throw new RuntimeException("Can only schedule from student proposed appointments");
        }

        if (appointment.getSuggestedAppointmentDate() == null ||
                appointment.getSuggestedTimeSlotStart() == null ||
                appointment.getSuggestedTimeSlotEnd() == null) {
            throw new RuntimeException("No suggestion found for this appointment");
        }

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

        appointment.setAppointmentDate(appointment.getSuggestedAppointmentDate());
        appointment.setTimeSlotStart(appointment.getSuggestedTimeSlotStart());
        appointment.setTimeSlotEnd(appointment.getSuggestedTimeSlotEnd());

        appointment.setSuggestedAppointmentDate(null);
        appointment.setSuggestedTimeSlotStart(null);
        appointment.setSuggestedTimeSlotEnd(null);

        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        if (appointment.getReportId() != null) {
            Report report = reportRepository.findById(appointment.getReportId()).orElse(null);
            if (report != null) {
                report.setStatus("scheduled");
                reportRepository.save(report);
            }
        }
    }

    private boolean timeOverlaps(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
