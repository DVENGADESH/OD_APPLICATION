package com.odapp.attendance.services;

import com.odapp.attendance.models.AttendanceLog;
import com.odapp.attendance.models.Event;
import com.odapp.attendance.models.Student;
import com.odapp.attendance.repositories.AttendanceLogRepository;
import com.odapp.attendance.repositories.EventRepository;
import com.odapp.attendance.repositories.StudentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchedulerService {

    private final AttendanceLogRepository logRepo;
    private final EventRepository eventRepo;
    private final StudentRepository studentRepo;
    private final EmailService emailService;
    private final Map<String, String> tutorEmails;
    private final Set<String> lowercaseBatchKeys;

    // Inject tutor email configuration from application.properties
    public SchedulerService(AttendanceLogRepository logRepo,
                            EventRepository eventRepo,
                            StudentRepository studentRepo,
                            EmailService emailService,
                            @Value("#{${app.tutor-emails}}") Map<String, String> tutorEmails) {
        this.logRepo = logRepo;
        this.eventRepo = eventRepo;
        this.studentRepo = studentRepo;
        this.emailService = emailService;
        this.tutorEmails = tutorEmails;
        this.lowercaseBatchKeys = tutorEmails.keySet().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        System.out.println("Scheduler initialized. Tutor Map Keys: " + tutorEmails.keySet()); // DEBUG 1
    }

    // Helper function to extract the batch key from the roll number (Case-Insensitive Fix)
    private String getBatchKey(String rollNumber) {
        if (rollNumber == null) return "default";

        final String lowerRollNumber = rollNumber.toLowerCase();

        // Find the original key (e.g., '23BIT') whose lowercase version matches
        // the start of the student's roll number (e.g., '23bit161').
        return tutorEmails.keySet().stream()
                .filter(originalKey -> lowerRollNumber.startsWith(originalKey.toLowerCase()))
                .findFirst()
                .orElse("default");
    }

    /**
     * This method runs automatically at 3:15 PM every day or manually.
     */
    @Scheduled(cron = "0 15 15 * * ?")
    public void sendOdReports() {
        LocalDate today = LocalDate.now();
        System.out.println("--- OD REPORT SCHEDULER STARTING ---"); // DEBUG 2

        // FIX (Temporary Debug Filter): Process any event that has not been marked completed.
        // This forces the scheduler to run the core report logic for testing.
        List<Event> eventsToProcess = eventRepo.findAll().stream()
                .filter(event -> !event.isCompleted())
                .collect(Collectors.toList());

        System.out.println("Events to process: " + eventsToProcess.size()); // DEBUG 3

        for (Event event : eventsToProcess) {
            System.out.println("Processing Event ID: " + event.getId() + ", Name: " + event.getEventName()); // DEBUG 4

            List<AttendanceLog> logs = logRepo.findAll().stream()
                    .filter(l -> Objects.equals(l.getEventId(), event.getId()))
                    .collect(Collectors.toList());

            Map<Long, List<AttendanceLog>> logsByStudentId =
                    logs.stream().collect(Collectors.groupingBy(AttendanceLog::getStudentId));

            int totalSessions = event.getSessions() != null ? event.getSessions().size() : 0;

            Map<String, Map<Student, List<AttendanceLog>>> reportsByTutor = new HashMap<>();
            int studentsQualified = 0;

            for (Map.Entry<Long, List<AttendanceLog>> entry : logsByStudentId.entrySet()) {
                Long studentId = entry.getKey();
                List<AttendanceLog> studentLogs = entry.getValue();

                long usedCount = studentLogs.stream()
                        .filter(AttendanceLog::isUsed)
                        .count();

                // If student checked in for ALL sessions -> OD granted
                if (totalSessions > 0 && usedCount >= totalSessions) {
                    studentsQualified++;
                    studentRepo.findById(studentId).ifPresent(student -> {
                        String rollNumber = student.getRollNumber();
                        String batchKey = getBatchKey(rollNumber);
                        String tutorEmail = tutorEmails.get(batchKey);

                        // DEBUG 5: Log the key resolution for qualified students
                        System.out.println("  -> Qualified Student: " + rollNumber +
                                ", BatchKey: " + batchKey +
                                ", TutorEmail: " + (tutorEmail != null ? tutorEmail : "NULL/NOT FOUND"));


                        // 1. Send confirmation to student
                        emailService.sendOdGrantedEmail(student.getEmail(), event.getEventName());

                        // 2. Prepare report for tutor
                        if (tutorEmail != null) {
                            reportsByTutor.computeIfAbsent(tutorEmail, k -> new HashMap<>())
                                    .put(student, studentLogs);
                        }
                    });
                }
            }

            System.out.println("Total Students Qualified for OD: " + studentsQualified); // DEBUG 6
            System.out.println("Total Tutor Reports to Dispatch: " + reportsByTutor.size()); // DEBUG 7

            // 3. Dispatch segmented reports to tutors
            reportsByTutor.forEach((tutorEmail, qualifiedStudentsMap) -> {
                System.out.println("Dispatching report to: " + tutorEmail + " for " + qualifiedStudentsMap.size() + " students."); // DEBUG 8
                emailService.sendOdReportToTutor(tutorEmail, event, qualifiedStudentsMap);
            });

            // Mark the event completed after processing
            if(!event.isCompleted()){
                event.setCompleted(true);
                eventRepo.save(event);
            }
            System.out.println("Event ID " + event.getId() + " processing complete."); // DEBUG 9
        }
    }

    /**
     * Manual trigger from OrganizerController / dashboard button.
     */
    public String generateODReports() {
        sendOdReports();
        return "OD reports generated and emails triggered (manual run).";
    }
}