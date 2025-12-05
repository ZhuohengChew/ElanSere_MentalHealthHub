package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.ForumPost;
import com.mentalhealthhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findAllByOrderByCreatedAtDesc();
    List<ForumPost> findByCategoryOrderByCreatedAtDesc(String category);
    long countByUser(User user);
}
