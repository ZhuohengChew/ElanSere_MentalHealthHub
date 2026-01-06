package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.EducationalModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EducationalModuleRepository extends JpaRepository<EducationalModule, Long> {
    List<EducationalModule> findByActiveTrueOrderByCreatedAtDesc();

    List<EducationalModule> findByCategoryAndActiveTrue(String category);

    List<EducationalModule> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title,
            String description);

    java.util.Optional<EducationalModule> findByTitle(String title);

    long countByActiveTrue();
}
