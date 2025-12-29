package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.ForumComment;
import com.mentalhealthhub.model.ForumPost;
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
}

