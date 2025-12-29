package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Query("SELECT a FROM AuditLog a WHERE (:userId IS NULL OR a.userId = :userId) ORDER BY a.createdAt DESC")
    Page<AuditLog> findAuditLogs(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId ORDER BY a.createdAt DESC")
    Page<AuditLog> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // Analytics queries
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.action = :action")
    Long countByAction(@Param("action") String action);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.createdAt > :dateTime")
    Long countByCreatedAtAfter(@Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT a.performedBy as adminId, COUNT(a) as count FROM AuditLog a GROUP BY a.performedBy ORDER BY count DESC")
    List<Object[]> getAdminActivityCount();

    @Query("SELECT FUNCTION('YEAR_WEEK', a.createdAt) as week, COUNT(a) FROM AuditLog a GROUP BY FUNCTION('YEAR_WEEK', a.createdAt) ORDER BY week DESC")
    Map<String, Long> getWeeklyActionTrend();

    @Query("SELECT FUNCTION('DATE_FORMAT', a.createdAt, '%Y-%m') as month, COUNT(a) FROM AuditLog a GROUP BY FUNCTION('DATE_FORMAT', a.createdAt, '%Y-%m') ORDER BY month DESC")
    Map<String, Long> getMonthlyActionTrend();
}
