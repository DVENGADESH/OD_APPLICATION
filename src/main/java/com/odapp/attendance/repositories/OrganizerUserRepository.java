package com.odapp.attendance.repositories;

import com.odapp.attendance.models.OrganizerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizerUserRepository extends JpaRepository<OrganizerUser, Long> {
    // Custom finder method for Spring Security to load user by username
    Optional<OrganizerUser> findByUsername(String username);
}
