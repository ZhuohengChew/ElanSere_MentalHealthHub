package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.ForumComment;
import com.mentalhealthhub.model.ForumPost;
import com.mentalhealthhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {
    List<ForumComment> findByPostOrderByCreatedAtAsc(ForumPost post);
    Long countByPost(ForumPost post);

    // Analytics queries
    @Query("SELECT FUNCTION('DATE_FORMAT', fc.createdAt, '%Y-%m') as month, COUNT(fc) FROM ForumComment fc GROUP BY FUNCTION('DATE_FORMAT', fc.createdAt, '%Y-%m') ORDER BY month DESC")
    Map<String, Long> getMonthlyCommentTrend();

    // Per-user comment counts for analytics (userId, count) ordered desc
    @Query("SELECT fc.user.id as userId, COUNT(fc) as count FROM ForumComment fc GROUP BY fc.user.id ORDER BY count DESC")
    List<Object[]> getUserCommentCounts();

    Long countByUser(User user);
}

