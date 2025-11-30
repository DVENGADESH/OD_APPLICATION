package com.odapp.attendance.services;

import com.odapp.attendance.models.AttendanceLog;
import com.odapp.attendance.models.Event;
import com.odapp.attendance.models.Student;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends initial check-in links to students upon registration.
     */
    @Async
    public void sendCheckinLinks(String toEmail, Event event, Map<String, String> tokens) {
        // Simple plain-text email with links; in production use HTML templates
        StringBuilder sb = new StringBuilder();
        sb.append("Hi,\n\nYou are registered for event: ").append(event.getEventName()).append("\n\n");
        sb.append("Use the following links to check-in for each session:\n\n");
        tokens.forEach((sessionId, token) -> {
            String link = String.format("http://localhost:8080/checkin?token=%s", token);
            sb.append("Session ").append(sessionId).append(": ").append(link).append("\n");
        });
        sb.append("\nRegards,\nOrganizer\n");

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Check-in links for " + event.getEventName());
        msg.setText(sb.toString());
        mailSender.send(msg);
    }

    /**
     * Dedicated method for sending OD Granted status after report generation.
     *
     * @param toEmail   The student's email.
     * @param eventName The name of the event.
     */
    public void sendOdGrantedEmail(String toEmail, String eventName) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("[Action Required] OD Granted for " + eventName);
        msg.setText("Dear Student,\n\nBased on your attendance at the '" + eventName + "' event, your OD has been granted. Please check with your tutor for the next steps.\n\nRegards,\nOD Coordinator");
        mailSender.send(msg);
    }

    /**
     * Sends a detailed OD report to a class tutor with improved formatting.
     */
    public void sendOdReportToTutor(String tutorEmail, Event event, Map<Student, List<AttendanceLog>> odGrantedStudents) {
        StringBuilder sb = new StringBuilder();

        // --- 1. Header and Event Details ---
        sb.append("OD Report Generated\n");
        sb.append("---------------------------------------------------\n");
        sb.append("Event Name:\t\t").append(event.getEventName()).append("\n");
        sb.append("Event Date:\t\t").append(event.getEventDate()).append("\n");
        sb.append("Total Qualified Students:\t").append(odGrantedStudents.size()).append("\n");
        sb.append("---------------------------------------------------\n\n");

        sb.append("DETAILS OF STUDENTS GRANTED OD:\n");
        sb.append("---------------------------------------------------\n");
        // Column Headers
        sb.append("Roll No\t\tName\t\t\tSessions Attended\n");
        sb.append("---------------------------------------------------\n");

        // --- 2. Student Details ---
        odGrantedStudents.forEach((student, logs) -> {

            String sessionsAttended = logs.stream()
                    .filter(AttendanceLog::isUsed)
                    .map(AttendanceLog::getSessionId)
                    .collect(Collectors.joining(", "));

            // Output student data with tabs for alignment
            sb.append(student.getRollNumber()).append("\t")
                    .append(student.getName()).append("\t\t")
                    .append(sessionsAttended).append("\n");
        });

        // --- 3. Footer ---
        sb.append("---------------------------------------------------\n");
        sb.append("\nThank you for your cooperation.\n");
        sb.append("Regards,\nOD Coordinator\n");

        // --- 4. Send Email ---
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(tutorEmail);
        msg.setSubject("[ACTION REQUIRED] OD Report for Event: " + event.getEventName());
        msg.setText(sb.toString());
        mailSender.send(msg);
    }
}