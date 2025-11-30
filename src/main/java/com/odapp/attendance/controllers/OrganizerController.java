package com.odapp.attendance.controllers;

import com.odapp.attendance.models.Event;
import com.odapp.attendance.services.EventService;
import com.odapp.attendance.services.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class OrganizerController {

    @Autowired
    private EventService eventService;

    @Autowired
    private SchedulerService schedulerService;


    // --- Thymeleaf View Controllers ---

    /**
     * Maps the root path (/) to the Organizer Dashboard page.
     * Fetches and attaches the latest events to the model for display.
     */
    @GetMapping("/")
    public String viewDashboard(Model model) {
        // Fetch the top 5 latest events for the dashboard table preview
        List<Event> latestEvents = eventService.getLatestEvents();
        model.addAttribute("events", latestEvents);
        return "dashboard"; // Renders the dashboard.html template
    }

    /**
     * Maps the path /register/{eventId} to render the student registration form.
     */
    @GetMapping("/register/{eventId}")
    public String viewRegisterPage(@PathVariable Long eventId) {
        return "register"; // Renders the register.html template
    }

    /**
     * Maps the path /checkin to render the check-in page.
     */
    @GetMapping("/checkin")
    public String viewCheckinPage() {
        return "checkin"; // Renders the checkin.html template
    }

    // --- Event Management APIs (POST/UPDATE) ---

    /**
     * API to create a new event from the Organizer Dashboard (JSON POST)
     * Note: This assumes the incoming request body has the shape of the Event model.
     */
    @PostMapping("/api/events/create") // <-- UNCOMMENTED THIS LINE
    @ResponseBody                      // <-- UNCOMMENTED THIS LINE
    public Long createEvent(@RequestBody Event event) { // <-- UNCOMMENTED THIS LINE
        // The service handles setting the default status and saving sessions via cascade.
        Event savedEvent = eventService.createEvent(event);
        return savedEvent.getId();
    }


    /**
     * API to mark an event as completed (used by the dashboard button).
     */
    @PostMapping("/api/events/complete/{eventId}")
    @ResponseBody
    public String completeEvent(@PathVariable Long eventId) {
        eventService.markEventCompleted(eventId);
        return "Event ID " + eventId + " marked as completed.";
    }


    // --- Scheduler Trigger (Manual Testing) ---

    /**
     * Manual endpoint to trigger the OD Report for immediate testing (called by the dashboard button).
     */
    @GetMapping("/admin/trigger-report")
    @ResponseBody // Tells Spring to return a string response, not a Thymeleaf page
    public String triggerReport() {
        schedulerService.generateODReports();
        return "OD Report triggered manually. Check console and tutor emails for report dispatch status.";
    }
}