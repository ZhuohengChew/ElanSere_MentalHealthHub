package com.mentalhealthhub.repository;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.User;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStudent(User student);
    List<Appointment> findByProfessional(User professional);

    List<Appointment> findByStudentOrProfessional(User student, User professional);

    // Analytics queries for admin dashboard
    @Query("SELECT COUNT(a) FROM Appointment a")
    Long countTotalAppointments();

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    Long countByStatus(@Param("status") com.mentalhealthhub.model.AppointmentStatus status);

    @Query("SELECT AVG(CAST(COUNT(a) AS DOUBLE)) FROM Appointment a GROUP BY a.student.id")
    Double getAverageAppointmentsPerStudent();

    @Query("SELECT a.professional.id, COUNT(a) FROM Appointment a WHERE a.professional IS NOT NULL GROUP BY a.professional.id ORDER BY COUNT(a) DESC")
    List<Object[]> getProfessionalWorkload();

    @Query(value = """
        SELECT DATE_FORMAT(appointment_date, '%Y-%m') AS month, COUNT(*) as count
        FROM appointments
        WHERE status = 'COMPLETED'
        GROUP BY DATE_FORMAT(appointment_date, '%Y-%m')
        ORDER BY month DESC
    """, nativeQuery = true)
    List<Object[]> getMonthlyCompletedAppointments();

    @Query(value = """
        SELECT HOUR(appointment_date) as hour, COUNT(*) as count
        FROM appointments
        GROUP BY HOUR(appointment_date)
    """, nativeQuery = true)
    List<Object[]> getPeakBookingTimes();

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate >= :startDate AND a.appointmentDate <= :endDate ORDER BY a.appointmentDate DESC")
    List<Appointment> findByDateRange(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
}
