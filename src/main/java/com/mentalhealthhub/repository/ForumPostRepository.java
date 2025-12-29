package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.ForumPost;
import com.mentalhealthhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findAllByOrderByCreatedAtDesc();
    List<ForumPost> findByCategoryOrderByCreatedAtDesc(String category);
    long countByUser(User user);

    // Analytics queries
    @Query("SELECT COUNT(fp) FROM ForumPost fp WHERE fp.createdAt > :dateTime")
    Long countByCreatedAtAfter(@Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT AVG(CAST(fp.views AS DOUBLE)) FROM ForumPost fp")
    Long getAverageViewsPerPost();

    @Query("SELECT AVG(CAST(fp.replies AS DOUBLE)) FROM ForumPost fp")
    Long getAverageRepliesPerPost();

    @Query("SELECT COUNT(DISTINCT fp.category) FROM ForumPost fp WHERE fp.category IS NOT NULL")
    Long getDistinctCategoryCount();

    @Query("SELECT fp.category as category, COUNT(fp) as count FROM ForumPost fp GROUP BY fp.category")
    List<Object[]> getPostsByCategory();

    @Query("SELECT fp FROM ForumPost fp ORDER BY fp.views DESC LIMIT :limit")
    List<ForumPost> getTopPostsByViews(@Param("limit") int limit);

    @Query("SELECT fp.user.id as userId, COUNT(fp) as count FROM ForumPost fp GROUP BY fp.user.id ORDER BY count DESC")
    List<Object[]> getUserParticipation();

    @Query("SELECT FUNCTION('DATE_FORMAT', fp.createdAt, '%Y-%m') as month, COUNT(fp) FROM ForumPost fp GROUP BY FUNCTION('DATE_FORMAT', fp.createdAt, '%Y-%m') ORDER BY month DESC")
    Map<String, Long> getMonthlyPostTrend();

    @Query(value = "SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(*) as count FROM forum_posts GROUP BY DATE_FORMAT(created_at, '%Y-%m') ORDER BY month DESC", nativeQuery = true)
    Map<String, Long> getMonthlyPostTrendNative();
}

