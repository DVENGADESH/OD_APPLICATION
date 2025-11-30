package com.odapp.attendance.controllers;

import com.odapp.attendance.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/register/{eventId}")
    public ResponseEntity<String> registerStudent(@PathVariable Long eventId,
                                                  @RequestBody RegistrationRequest request) {
        try {
            String result = eventService.registerStudent(eventId, request.rollNumber(), request.name(), request.email());
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            // FIX: Check for the expected business exception to prevent rollback error
            if (ex.getMessage().equals("Already registered.")) {
                // Return 200 OK for idempotence (desired state achieved)
                return ResponseEntity.ok("Already registered.");
            }
            // For other exceptions (like "Event not found" or real DB errors), return 400 BAD_REQUEST
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/checkin")
    public ResponseEntity<String> checkIn(@RequestParam String token,
                                          @RequestParam double lat,
                                          @RequestParam double lon) {
        try {
            String res = eventService.checkIn(token, lat, lon);
            return ResponseEntity.ok(res);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    private record RegistrationRequest(String rollNumber, String name, String email) {}
}