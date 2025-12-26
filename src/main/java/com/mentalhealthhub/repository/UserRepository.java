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
}
