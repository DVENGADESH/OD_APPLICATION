package com.odapp.attendance.controllers;

import com.odapp.attendance.models.AttendanceLog;
import com.odapp.attendance.repositories.AttendanceLogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceLogRepository repo;

    public AttendanceController(AttendanceLogRepository repo) {
        this.repo = repo;
    }

    // Removed the confusing markCompleted method. Event completion is in OrganizerController.

    // Endpoint to retrieve all attendance logs, e.g., for reporting purposes
    @GetMapping("/ordered")
    public List<AttendanceLog> getAllOrdered() {
        // Retrieves all attendance logs sorted by ID
        return repo.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}