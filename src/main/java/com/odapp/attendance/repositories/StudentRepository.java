package com.odapp.attendance.repositories;

import com.odapp.attendance.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumberAndEventId(String rollNumber, Long eventId);
}
