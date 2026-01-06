package com.mentalhealthhub.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mentalhealthhub.model.Report;
import com.mentalhealthhub.model.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // Basic queries
    List<Report> findByStatus(String status);
    List<Report> findByUrgency(String urgency);
    List<Report> findByStudent(User student);
    List<Report> findByStudentId(Long studentId);

    // History and transaction queries
    List<Report> findByStatusAndStudent(String status, User student);
    List<Report> findByStudentOrderBySubmittedAtDesc(User student);
    List<Report> findByStudentIdOrderBySubmittedAtDesc(Long studentId);
    
    // Date range queries for reporting
    @Query("SELECT r FROM Report r WHERE r.submittedAt BETWEEN :startDate AND :endDate")
    List<Report> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM Report r WHERE r.student.id = :studentId AND r.submittedAt BETWEEN :startDate AND :endDate")
    List<Report> findByStudentAndDateRange(@Param("studentId") Long studentId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    // Status-based reporting
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.student.id = :studentId AND r.status = :status")
    long countByStudentAndStatus(@Param("studentId") Long studentId, @Param("status") String status);

    // Urgency-based reporting
    @Query("SELECT COUNT(r) FROM Report r WHERE r.urgency = :urgency")
    long countByUrgency(@Param("urgency") String urgency);

    @Query("SELECT r FROM Report r WHERE r.urgency = :urgency ORDER BY r.submittedAt DESC")
    List<Report> findByUrgencyOrderBySubmittedAtDesc(@Param("urgency") String urgency);

    // Combined filtering
    @Query("SELECT r FROM Report r WHERE r.student.id = :studentId AND r.status = :status ORDER BY r.submittedAt DESC")
    List<Report> findByStudentAndStatus(@Param("studentId") Long studentId, @Param("status") String status);

    @Query("SELECT r FROM Report r WHERE r.student.id = :studentId AND r.urgency = :urgency ORDER BY r.submittedAt DESC")
    List<Report> findByStudentAndUrgency(@Param("studentId") Long studentId, @Param("urgency") String urgency);

    // Statistics queries
    @Query("SELECT r FROM Report r WHERE r.status != 'resolved' AND r.status != 'closed' ORDER BY r.urgency DESC, r.submittedAt ASC")
    List<Report> findOpenReportsOrderedByUrgency();

    @Query("SELECT COUNT(r) FROM Report r WHERE r.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);

    // Find by resolved status for history
    @Query("SELECT r FROM Report r WHERE r.status = 'resolved' OR r.status = 'closed' ORDER BY r.resolvedAt DESC")
    List<Report> findResolvedReports();

    // Analytics queries
    @Query("SELECT COUNT(r) FROM Report r WHERE r.submittedAt > :dateTime")
    Long countBySubmittedAtAfter(@Param("dateTime") LocalDateTime dateTime);

    @Query(value = "SELECT AVG(" +
            "TIMESTAMPDIFF(SECOND, r.submitted_at, r.resolved_at)" +
            ") / 3600 " +
            "FROM reports r " +
            "WHERE r.resolved_at IS NOT NULL",
            nativeQuery = true)
    Double getAverageResolutionTimeHours();

    @Query("SELECT r.type as type, COUNT(r) as count FROM Report r GROUP BY r.type")
    List<Object[]> getReportsByType();

    @Query(value = "SELECT DATE_FORMAT(r.submitted_at, '%Y-%m') AS month, COUNT(*) " +
            "FROM reports r " +
            "GROUP BY DATE_FORMAT(r.submitted_at, '%Y-%m') " +
            "ORDER BY month DESC",
            nativeQuery = true)
    List<Object[]> getMonthlyReportTrend();

    @Query(value = "SELECT DATE_FORMAT(r.resolved_at, '%Y-%m') AS month, COUNT(*) " +
            "FROM reports r " +
            "WHERE r.resolved_at IS NOT NULL " +
            "GROUP BY DATE_FORMAT(r.resolved_at, '%Y-%m') " +
            "ORDER BY month DESC",
            nativeQuery = true)
    List<Object[]> getMonthlyResolutions();
}
