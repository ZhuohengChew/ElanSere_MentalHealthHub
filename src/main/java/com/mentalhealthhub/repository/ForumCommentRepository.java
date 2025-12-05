package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.ForumComment;
import com.mentalhealthhub.model.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {
    List<ForumComment> findByPostOrderByCreatedAtAsc(ForumPost post);
    Long countByPost(ForumPost post);
}

