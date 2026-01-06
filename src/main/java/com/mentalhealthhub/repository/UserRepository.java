package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.User;
import com.mentalhealthhub.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import java.util.Map;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);

    @Query("SELECT u FROM User u " +
            "WHERE u.deletedAt IS NULL " +
            "AND (:q IS NULL OR LOWER(u.name) LIKE %:q% OR LOWER(u.email) LIKE %:q%) " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:active IS NULL OR u.active = :active)")
    Page<User> searchUsers(@Param("q") String q,
                           @Param("role") UserRole role,
                           @Param("active") Boolean active,
                           Pageable pageable);

    // Analytics queries for admin dashboard
    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL")
    Long countTotalUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true AND u.deletedAt IS NULL")
    Long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = false AND u.deletedAt IS NULL")
    Long countInactiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.deletedAt IS NULL")
    Long countByRole(@Param("role") UserRole role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt > :dateTime AND u.deletedAt IS NULL")
    Long countNewUsersSince(@Param("dateTime") java.time.LocalDateTime dateTime);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.deletedAt IS NULL ORDER BY u.createdAt DESC")
    List<User> findAllActiveUsers();

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.deletedAt IS NULL")
    List<User> findByRole(@Param("role") UserRole role);

    @Query("SELECT AVG(CAST(u.stressLevel AS DOUBLE)) FROM User u WHERE u.stressLevel IS NOT NULL AND u.deletedAt IS NULL")
    Double getAverageStressLevel();

    @Query("SELECT AVG(CAST(u.anxietyLevel AS DOUBLE)) FROM User u WHERE u.anxietyLevel IS NOT NULL AND u.deletedAt IS NULL")
    Double getAverageAnxietyLevel();

    @Query("SELECT AVG(CAST(u.wellbeingScore AS DOUBLE)) FROM User u WHERE u.wellbeingScore IS NOT NULL AND u.deletedAt IS NULL")
    Double getAverageWellbeingScore();

    @Query(value = "SELECT DATE_FORMAT(u.created_at, '%Y-%m') as month, COUNT(u.id) FROM users u WHERE u.deleted_at IS NULL GROUP BY DATE_FORMAT(u.created_at, '%Y-%m') ORDER BY month DESC", nativeQuery = true)
    java.util.Map<String, Long> getUserRegistrationTrend();
}
