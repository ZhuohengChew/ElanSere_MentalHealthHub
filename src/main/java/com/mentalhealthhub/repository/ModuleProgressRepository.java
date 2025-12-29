package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.EducationalModule;
import com.mentalhealthhub.model.ModuleProgress;
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
public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, Long> {

    List<ModuleProgress> findByUser(User user);

    Optional<ModuleProgress> findByUserAndModule(User user, EducationalModule module);

    List<ModuleProgress> findByUserAndCompletedTrue(User user);

    Long countByUserAndCompletedTrue(User user);

    // Analytics queries
    Long countByCompleted(Boolean completed);

    @Query("SELECT COUNT(mp) FROM ModuleProgress mp WHERE mp.completedAt > :dateTime")
    Long countByCompletedAtAfter(@Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT AVG(CAST(mp.progressPercentage AS DOUBLE)) FROM ModuleProgress mp WHERE mp.progressPercentage IS NOT NULL")
    Double getAverageProgressPercentage();

    @Query("SELECT mp.module.id as key, COUNT(mp) as value FROM ModuleProgress mp WHERE mp.completed = true GROUP BY mp.module.id")
    Map<Long, Long> getCompletionRatesPerModule();

    @Query("SELECT mp.module.id as key, AVG(CAST(mp.progressPercentage AS DOUBLE)) as value FROM ModuleProgress mp GROUP BY mp.module.id")
    Map<Long, Double> getAverageProgressPerModule();

    @Query(value = "SELECT DATE_FORMAT(completed_at, '%Y-%m') as month, COUNT(*) as count " +
            "FROM module_progress " +
            "WHERE completed = true " +
            "GROUP BY DATE_FORMAT(completed_at, '%Y-%m') " +
            "ORDER BY month DESC",
            nativeQuery = true)
    List<Object[]> getMonthlyCompletionTrend();

    @Query("SELECT m.title, COUNT(CASE WHEN mp.completed = true THEN 1 END) as completions FROM ModuleProgress mp JOIN mp.module m GROUP BY m.id ORDER BY completions DESC")
    List<Object[]> getMostAccessedModules();

    @Query("SELECT COUNT(DISTINCT mp.user.id) FROM ModuleProgress mp WHERE mp.module.id = :moduleId AND mp.completed = true")
    Long countUniqueCompletionsByModule(@Param("moduleId") Long moduleId);

    void deleteByModule(EducationalModule module);
}
