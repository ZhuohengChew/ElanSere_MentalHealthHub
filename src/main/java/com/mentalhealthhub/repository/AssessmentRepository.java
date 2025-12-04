package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.Assessment;
import com.mentalhealthhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByUser(User user);

    List<Assessment> findByUserOrderByCreatedAtDesc(User user);

    List<Assessment> findByUserOrderByCompletedAtDesc(User user);

    Optional<Assessment> findFirstByUserOrderByCompletedAtDesc(User user);

    Long countByUser(User user);
}
