package com.odapp.attendance.services;

import com.odapp.attendance.models.AttendanceLog;
import com.odapp.attendance.models.Event;
import com.odapp.attendance.models.Session;
import com.odapp.attendance.models.Student;
import com.odapp.attendance.repositories.AttendanceLogRepository;
import com.odapp.attendance.repositories.EventRepository;
import com.odapp.attendance.repositories.StudentRepository;
import com.odapp.attendance.utils.DistanceUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EventService {

    private final StudentRepository studentRepository;
    private final EventRepository eventRepository;
    private final AttendanceLogRepository logRepository;
    private final EmailService emailService;

    @Value("${app.geofence.radius-km:0.5}")
    private double GEOFENCE_RADIUS_KM;

    public EventService(StudentRepository studentRepository, EventRepository eventRepository,
                        AttendanceLogRepository logRepository, EmailService emailService) {
        this.studentRepository = studentRepository;
        this.eventRepository = eventRepository;
        this.logRepository = logRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Event createEvent(Event event) {
        if (event.getSessions() != null) {
            // Ensures bi-directional link is set before saving
            event.getSessions().forEach(s -> s.setEvent(event));
        }
        event.setCompleted(false);
        return eventRepository.save(event);
    }

    @Transactional
    public String registerStudent(Long eventId, String rollNumber, String name, String email) {
        // 1. Initial check: If already present, throw a controlled exception
        Optional<Student> ex = studentRepository.findByRollNumberAndEventId(rollNumber, eventId);
        if (ex.isPresent()) {
            throw new RuntimeException("Already registered.");
        }

        Student s = new Student();
        s.setRollNumber(rollNumber);
        s.setName(name);
        s.setEmail(email);
        s.setEventId(eventId);

        try {
            // Attempt to save the student
            s = studentRepository.save(s);
        } catch (DataIntegrityViolationException e) {
            // 2. Race condition fix: Catch database error if another request beat this one
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                throw new RuntimeException("Already registered.");
            }
            // Re-throw other integrity issues
            throw new RuntimeException("Database error during registration.", e);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 3. Generate tokens and logs only after successful student save
        Map<String,String> tokens = new HashMap<>();
        for (Session session : event.getSessions()) {
            String token = UUID.randomUUID().toString();
            AttendanceLog log = new AttendanceLog();
            log.setStudentId(s.getId());
            log.setEventId(eventId);
            log.setSessionId(session.getSessionId());
            log.setUniqueToken(token);
            logRepository.save(log);
            tokens.put(session.getSessionId(), token);
        }

        emailService.sendCheckinLinks(email, event, tokens);
        return "Registered successfully.";
    }

    @Transactional
    public String checkIn(String token, double lat, double lon) {
        Optional<AttendanceLog> opt = logRepository.findByUniqueToken(token);
        if (opt.isEmpty()) return "Invalid token";

        AttendanceLog log = opt.get();
        if (log.isUsed()) return "Token already used";

        Event event = eventRepository.findById(log.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 🚀 FIX: Check if event coordinates are valid before calculation
        if (event.getLocationLat() == null || event.getLocationLon() == null) {
            log.setUsed(true);
            log.setFraudulent(true);
            log.setCheckinTime(LocalDateTime.now());
            logRepository.save(log);
            return "Check-in denied. Event location is missing/invalid.";
        }

        double distance = DistanceUtils.calculateDistance(lat, lon, event.getLocationLat(), event.getLocationLon());

        if (distance > GEOFENCE_RADIUS_KM) {
            log.setUsed(true);
            log.setFraudulent(true);
            log.setCheckinTime(LocalDateTime.now());
            logRepository.save(log);
            return String.format("Check-in denied. You're %.2f km away (max allowed: %.2f km).", distance, GEOFENCE_RADIUS_KM);
        }

        log.setUsed(true);
        log.setCheckinTime(LocalDateTime.now());
        log.setCheckinLat(lat);
        log.setCheckinLon(lon);
        logRepository.save(log);
        return "Check-in successful";
    }

    public List<Event> getLatestEvents() {
        return eventRepository.findTop5ByOrderByEventDateDesc();
    }

    @Transactional
    public void markEventCompleted(Long eventId) {
        Event e = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Not found"));
        e.setCompleted(true);
        eventRepository.save(e);
    }
}