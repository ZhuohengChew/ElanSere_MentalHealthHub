package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.Content;
import com.mentalhealthhub.model.ContentStatus;
import com.mentalhealthhub.model.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByStatus(ContentStatus status);
    List<Content> findByType(ContentType type);
    List<Content> findByCategory(String category);
    List<Content> findByStatusAndType(ContentStatus status, ContentType type);
    List<Content> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}

