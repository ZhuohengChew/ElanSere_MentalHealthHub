package com.mentalhealthhub.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private User professional;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @Column(nullable = false)
    private LocalTime timeSlotStart;

    @Column(nullable = false)
    private LocalTime timeSlotEnd;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AppointmentStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String meetingLink;

    @Column(nullable = true)
    private Long reportId;

    // Student suggestion fields
    @Column(nullable = true)
    private LocalDate suggestedAppointmentDate;

    @Column(nullable = true)
    private LocalTime suggestedTimeSlotStart;

    @Column(nullable = true)
    private LocalTime suggestedTimeSlotEnd;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Appointment() {
    }

    public Appointment(Long id, User student, User professional, LocalDate appointmentDate,
            LocalTime timeSlotStart, LocalTime timeSlotEnd, AppointmentStatus status, String notes, 
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.student = student;
        this.professional = professional;
        this.appointmentDate = appointmentDate;
        this.timeSlotStart = timeSlotStart;
        this.timeSlotEnd = timeSlotEnd;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public User getProfessional() {
        return professional;
    }

    public void setProfessional(User professional) {
        this.professional = professional;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getTimeSlotStart() {
        return timeSlotStart;
    }

    public void setTimeSlotStart(LocalTime timeSlotStart) {
        this.timeSlotStart = timeSlotStart;
    }

    public LocalTime getTimeSlotEnd() {
        return timeSlotEnd;
    }

    public void setTimeSlotEnd(LocalTime timeSlotEnd) {
        this.timeSlotEnd = timeSlotEnd;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public LocalDate getSuggestedAppointmentDate() {
        return suggestedAppointmentDate;
    }

    public void setSuggestedAppointmentDate(LocalDate suggestedAppointmentDate) {
        this.suggestedAppointmentDate = suggestedAppointmentDate;
    }

    public LocalTime getSuggestedTimeSlotStart() {
        return suggestedTimeSlotStart;
    }

    public void setSuggestedTimeSlotStart(LocalTime suggestedTimeSlotStart) {
        this.suggestedTimeSlotStart = suggestedTimeSlotStart;
    }

    public LocalTime getSuggestedTimeSlotEnd() {
        return suggestedTimeSlotEnd;
    }

    public void setSuggestedTimeSlotEnd(LocalTime suggestedTimeSlotEnd) {
        this.suggestedTimeSlotEnd = suggestedTimeSlotEnd;
    }

    public static class Builder {
        private Long id;
        private User student;
        private User professional;
        private LocalDate appointmentDate;
        private LocalTime timeSlotStart;
        private LocalTime timeSlotEnd;
        private AppointmentStatus status;
        private String notes;
        private Long reportId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder student(User student) {
            this.student = student;
            return this;
        }

        public Builder professional(User professional) {
            this.professional = professional;
            return this;
        }

        public Builder appointmentDate(LocalDate appointmentDate) {
            this.appointmentDate = appointmentDate;
            return this;
        }

        public Builder timeSlotStart(LocalTime timeSlotStart) {
            this.timeSlotStart = timeSlotStart;
            return this;
        }

        public Builder timeSlotEnd(LocalTime timeSlotEnd) {
            this.timeSlotEnd = timeSlotEnd;
            return this;
        }

        public Builder status(AppointmentStatus status) {
            this.status = status;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder reportId(Long reportId) {
            this.reportId = reportId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Appointment build() {
            Appointment appointment = new Appointment(id, student, professional, appointmentDate, timeSlotStart, 
                timeSlotEnd, status, notes, createdAt, updatedAt);
            appointment.setReportId(reportId);
            return appointment;
        }
    }
}
