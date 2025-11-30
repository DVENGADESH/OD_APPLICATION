package com.odapp.attendance.config;

import com.odapp.attendance.services.OrganizerUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // <-- CRITICAL MISSING IMPORT

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // 1. Define PasswordEncoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Define AuthenticationManager Bean
    @Bean
    public AuthenticationManager authenticationManager(
            // FIX: HttpSecurity must be imported for this line to compile
            HttpSecurity http,
            PasswordEncoder passwordEncoder,
            OrganizerUserDetailsService userDetailsService) throws Exception {

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }
}