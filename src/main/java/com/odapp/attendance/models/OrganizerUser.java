package com.odapp.attendance.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class OrganizerUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Stored as BCrypt hash

    private String role = "ADMIN"; // Default role

    // Constructor for easy initial setup
    public OrganizerUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
}