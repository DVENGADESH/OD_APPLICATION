package com.odapp.attendance.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_logs")
public class AttendanceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long eventId;
    private String sessionId;

    @Column(unique = true, length = 150)
    private String uniqueToken;

    private boolean used = false;
    private boolean fraudulent = false;

    // 👇 Added to satisfy log.setCompleted(...)
    private boolean completed = false;

    private LocalDateTime checkinTime;
    private Double checkinLat;
    private Double checkinLon;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getUniqueToken() { return uniqueToken; }
    public void setUniqueToken(String uniqueToken) { this.uniqueToken = uniqueToken; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public boolean isFraudulent() { return fraudulent; }
    public void setFraudulent(boolean fraudulent) { this.fraudulent = fraudulent; }

    // 👇 New getter/setter
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCheckinTime() { return checkinTime; }
    public void setCheckinTime(LocalDateTime checkinTime) { this.checkinTime = checkinTime; }

    public Double getCheckinLat() { return checkinLat; }
    public void setCheckinLat(Double checkinLat) { this.checkinLat = checkinLat; }

    public Double getCheckinLon() { return checkinLon; }
    public void setCheckinLon(Double checkinLon) { this.checkinLon = checkinLon; }
}
