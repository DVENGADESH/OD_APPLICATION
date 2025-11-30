package com.odapp.attendance.config;

import com.odapp.attendance.models.OrganizerUser;
import com.odapp.attendance.repositories.OrganizerUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InitialDataSeeder {

    @Bean
    CommandLineRunner initializeDefaultUser(OrganizerUserRepository repo, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if default user exists
            if (repo.findByUsername("organizer").isEmpty()) {

                // WARNING: Never store plain text passwords. They must be HASHED.
                String hashedPassword = passwordEncoder.encode("password");

                OrganizerUser defaultUser = new OrganizerUser("organizer", hashedPassword);
                // Set role (although ADMIN is the default in the model)
                defaultUser.setRole("ADMIN");
                repo.save(defaultUser);
                System.out.println("✅ Default Organizer user created: username=organizer, password=password (hashed)");
            }
        };
    }
}