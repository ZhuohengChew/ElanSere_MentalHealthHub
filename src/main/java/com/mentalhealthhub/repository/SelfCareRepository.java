package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.SelfCare;
import com.mentalhealthhub.model.SelfCareType;
import com.mentalhealthhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface SelfCareRepository extends JpaRepository<SelfCare, Long> {
    List<SelfCare> findByUser(User user);
    List<SelfCare> findByUserAndType(User user, SelfCareType type);
    List<SelfCare> findByUserAndActivityDate(User user, LocalDate date);
    List<SelfCare> findByUserAndActivityDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<SelfCare> findByUserAndTypeAndActivityDateBetween(User user, SelfCareType type, LocalDate startDate, LocalDate endDate);
    List<SelfCare> findByUserAndTypeAndActivityDate(User user, SelfCareType type, LocalDate date);
    List<SelfCare> findByUserOrderByActivityDateDesc(User user);
    List<SelfCare> findByUserAndTypeOrderByActivityDateDesc(User user, SelfCareType type);

    // Analytics queries
    @Query(value = "SELECT COUNT(*) FROM self_care WHERE type = :type", nativeQuery = true)
    Long countByType(@Param("type") String type);

    @Query(value = "SELECT COUNT(*) FROM self_care WHERE mood = :mood", nativeQuery = true)
    Long countByMood(@Param("mood") String mood);

    @Query("SELECT COUNT(sc) FROM SelfCare sc WHERE sc.activityDate > :date")
    Long countByActivityDateAfter(@Param("date") LocalDate date);

    @Query("SELECT FUNCTION('DATE_FORMAT', sc.activityDate, '%Y-%m-%d') as day, COUNT(sc) FROM SelfCare sc GROUP BY FUNCTION('DATE_FORMAT', sc.activityDate, '%Y-%m-%d') ORDER BY day DESC")
    Map<String, Long> getDailyActivityTrend();

    @Query("SELECT FUNCTION('YEAR_WEEK', sc.activityDate) as week, COUNT(sc) FROM SelfCare sc GROUP BY FUNCTION('YEAR_WEEK', sc.activityDate) ORDER BY week DESC")
    Map<String, Long> getWeeklyActivityTrend();

    @Query("SELECT FUNCTION('DATE_FORMAT', sc.activityDate, '%Y-%m') as month, COUNT(sc) FROM SelfCare sc GROUP BY FUNCTION('DATE_FORMAT', sc.activityDate, '%Y-%m') ORDER BY month DESC")
    Map<String, Long> getMonthlyActivityTrend();
}

