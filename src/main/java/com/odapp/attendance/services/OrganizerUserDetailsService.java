package com.odapp.attendance.services;

import com.odapp.attendance.models.OrganizerUser;
import com.odapp.attendance.repositories.OrganizerUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OrganizerUserDetailsService implements UserDetailsService {

    private final OrganizerUserRepository organizerUserRepository;

    public OrganizerUserDetailsService(OrganizerUserRepository organizerUserRepository) {
        this.organizerUserRepository = organizerUserRepository;
    }

    /**
     * Locates the user based on the username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        OrganizerUser organizer = organizerUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Organizer user not found with username: " + username));

        // Create a Spring Security UserDetails object from your OrganizerUser
        return new User(
                organizer.getUsername(),
                organizer.getPassword(), // The stored password (BCrypt hash)
                Collections.emptyList() // Simple user, no specific authorities/roles for now
        );
    }
}