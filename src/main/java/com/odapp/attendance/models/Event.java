package com.odapp.attendance.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    private Double locationLat;
    private Double locationLon;

    private boolean completed = false;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Session> sessions = new ArrayList<>();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public Double getLocationLat() { return locationLat; }
    public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }

    public Double getLocationLon() { return locationLon; }
    public void setLocationLon(Double locationLon) { this.locationLon = locationLon; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public List<Session> getSessions() { return sessions; }
    public void setSessions(List<Session> sessions) {
        this.sessions = sessions == null ? new ArrayList<>() : sessions;
        this.sessions.forEach(s -> { if (s != null) s.setEvent(this); });
    }
}
