package com.odapp.attendance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ADD /api/events/create to the list of paths ignoring CSRF checks
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        new AntPathRequestMatcher("/api/events/register/**"),
                        new AntPathRequestMatcher("/api/events/checkin"),
                        new AntPathRequestMatcher("/api/events/complete/**"),
                        new AntPathRequestMatcher("/api/events/create"), // <-- ADD THIS LINE
                        new AntPathRequestMatcher("/register/**")
                ))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                new AntPathRequestMatcher("/register/**"),
                                new AntPathRequestMatcher("/checkin"),
                                new AntPathRequestMatcher("/api/events/register/**"),
                                new AntPathRequestMatcher("/api/events/checkin")
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.permitAll());

        return http.build();
    }
}