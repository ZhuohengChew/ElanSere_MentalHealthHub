package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.EducationalModule;
import com.mentalhealthhub.model.ModuleProgress;
import com.mentalhealthhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, Long> {
    List<ModuleProgress> findByUser(User user);
    Optional<ModuleProgress> findByUserAndModule(User user, EducationalModule module);
    List<ModuleProgress> findByUserAndCompletedTrue(User user);
    Long countByUserAndCompletedTrue(User user);
}

