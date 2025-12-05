package com.mentalhealthhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentalhealthhub.model.Appointment;
import com.mentalhealthhub.model.User;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStudent(User student);
    List<Appointment> findByProfessional(User professional);

    List<Appointment> findByStudentOrProfessional(User student, User professional);
}
