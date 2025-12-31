package com.mentalhealthhub.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.AppointmentStatus;
import com.mentalhealthhub.model.User;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStudent(User student);
    List<Appointment> findByProfessional(User professional);
    
    // Find appointments by report ID
    Appointment findByReportId(Long reportId);
    
    // Find appointments by report ID with eager loading of professional
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.professional WHERE a.reportId = :reportId")
    Appointment findByReportIdWithProfessional(@Param("reportId") Long reportId);

    List<Appointment> findByStudentOrProfessional(User student, User professional);

    // Status-based queries
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByStudentAndStatus(User student, AppointmentStatus status);
    List<Appointment> findByProfessionalAndStatus(User professional, AppointmentStatus status);
    List<Appointment> findByStudentAndStatusOrderByAppointmentDateDesc(User student, AppointmentStatus status);
    List<Appointment> findByProfessionalAndStatusOrderByAppointmentDateDesc(User professional, AppointmentStatus status);
    
    // Date and time based queries
    List<Appointment> findByAppointmentDateAndProfessional(LocalDate date, User professional);
    List<Appointment> findByAppointmentDateAndProfessionalAndStatus(LocalDate date, User professional, AppointmentStatus status);
    List<Appointment> findByAppointmentDateAndStudentAndStatus(LocalDate date, User student, AppointmentStatus status);
    
    // Query to find occupied slots by professional ID
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date AND a.professional.id = :professionalId AND a.status = :status")
    List<Appointment> findByAppointmentDateAndProfessionalIdAndStatus(
        @Param("date") LocalDate date,
        @Param("professionalId") Long professionalId,
        @Param("status") AppointmentStatus status
    );
    
    // Check for occupied slots
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date AND a.professional = :professional " +
           "AND a.status IN ('PENDING', 'APPROVED') " +
           "AND ((a.timeSlotStart < :slotEnd AND a.timeSlotEnd > :slotStart))")
    List<Appointment> findConflictingAppointments(
        @Param("date") LocalDate date,
        @Param("professional") User professional,
        @Param("slotStart") LocalTime slotStart,
        @Param("slotEnd") LocalTime slotEnd
    );

    // Analytics queries for admin dashboard
    @Query("SELECT COUNT(a) FROM Appointment a")
    Long countTotalAppointments();

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    Long countByStatus(@Param("status") AppointmentStatus status);

    @Query("SELECT AVG(CAST(COUNT(a) AS DOUBLE)) FROM Appointment a GROUP BY a.student.id")
    Double getAverageAppointmentsPerStudent();

    @Query("SELECT a.professional.id, COUNT(a) FROM Appointment a WHERE a.professional IS NOT NULL GROUP BY a.professional.id ORDER BY COUNT(a) DESC")
    List<Object[]> getProfessionalWorkload();

    @Query(value = """
        SELECT DATE_FORMAT(appointment_date, '%Y-%m') AS month, COUNT(*) as count
        FROM appointments
        WHERE status = 'APPROVED'
        GROUP BY DATE_FORMAT(appointment_date, '%Y-%m')
        ORDER BY month DESC
    """, nativeQuery = true)
    List<Object[]> getMonthlyCompletedAppointments();

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate >= :startDate AND a.appointmentDate <= :endDate ORDER BY a.appointmentDate DESC")
    List<Appointment> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
