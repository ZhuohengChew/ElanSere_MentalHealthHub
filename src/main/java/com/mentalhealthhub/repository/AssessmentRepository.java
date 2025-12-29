package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.Assessment;
import com.mentalhealthhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByUser(User user);

    List<Assessment> findByUserOrderByCreatedAtDesc(User user);

    List<Assessment> findByUserOrderByCompletedAtDesc(User user);

    Optional<Assessment> findFirstByUserOrderByCompletedAtDesc(User user);

    Long countByUser(User user);

    // Analytics queries
    @Query("SELECT COUNT(a) FROM Assessment a WHERE a.completedAt > :dateTime")
    Long countByCompletedAtAfter(@Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT AVG(CAST(a.totalScore AS DOUBLE)) FROM Assessment a")
    Double getAverageMentalHealthScore();

    @Query("SELECT a.category, COUNT(a) FROM Assessment a GROUP BY a.category")
    List<Object[]> getDistributionByCategory();

    @Query("SELECT AVG(CAST(a.totalScore AS DOUBLE)) FROM Assessment a WHERE a.category = :category")
    Double getAverageScoreByCategory(@Param("category") String category);

    @Query(value = "SELECT " +
            "SUM(CASE WHEN total_score >= 0 AND total_score <= 20 THEN 1 ELSE 0 END) as low, " +
            "SUM(CASE WHEN total_score >= 21 AND total_score <= 40 THEN 1 ELSE 0 END) as mild, " +
            "SUM(CASE WHEN total_score >= 41 AND total_score <= 60 THEN 1 ELSE 0 END) as moderate, " +
            "SUM(CASE WHEN total_score >= 61 AND total_score <= 100 THEN 1 ELSE 0 END) as high " +
            "FROM assessments",
            nativeQuery = true)
    Object getScoreDistribution();

    @Query(value = "SELECT DATE_FORMAT(completed_at, '%Y-%m') AS month, COUNT(*) as count " +
            "FROM assessments " +
            "GROUP BY DATE_FORMAT(completed_at, '%Y-%m') " +
            "ORDER BY month DESC",
            nativeQuery = true)
    List<Object[]> getMonthlyCompletionTrend();

    @Query(value = "SELECT DATE_FORMAT(completed_at, '%Y-%m') AS month, AVG(total_score) as avgScore " +
            "FROM assessments " +
            "GROUP BY DATE_FORMAT(completed_at, '%Y-%m') " +
            "ORDER BY month DESC",
            nativeQuery = true)
    List<Object[]> getMentalHealthScoreTrend();
}
