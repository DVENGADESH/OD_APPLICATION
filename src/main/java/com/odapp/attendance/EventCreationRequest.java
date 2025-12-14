package com.odapp.attendance.dto;

import java.util.List;

/**
 * Request DTO matching dashboard.js payload:
 * {
 *   eventName, eventDate (yyyy-MM-dd), locationLat, locationLon, sessions: [{ sessionId, sessionName, startTime, endTime }]
 * }
 */
public record EventCreationRequest(
        String eventName,
        String eventDate,
        Double locationLat,
        Double locationLon,
        List<SessionDTO> sessions
) {
    public static record SessionDTO(String sessionId, String sessionName, String startTime, String endTime) {}
}
