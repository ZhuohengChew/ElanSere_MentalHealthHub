package com.mentalhealthhub.repository;

import com.mentalhealthhub.model.SelfCare;
import com.mentalhealthhub.model.SelfCareType;
import com.mentalhealthhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SelfCareRepository extends JpaRepository<SelfCare, Long> {
    List<SelfCare> findByUser(User user);
    List<SelfCare> findByUserAndType(User user, SelfCareType type);
    List<SelfCare> findByUserAndActivityDate(User user, LocalDate date);
    List<SelfCare> findByUserAndActivityDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<SelfCare> findByUserAndTypeAndActivityDateBetween(User user, SelfCareType type, LocalDate startDate, LocalDate endDate);
    List<SelfCare> findByUserAndTypeAndActivityDate(User user, SelfCareType type, LocalDate date);
    List<SelfCare> findByUserOrderByActivityDateDesc(User user);
    List<SelfCare> findByUserAndTypeOrderByActivityDateDesc(User user, SelfCareType type);
}

