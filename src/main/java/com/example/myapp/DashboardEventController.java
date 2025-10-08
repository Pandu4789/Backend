package com.example.myapp;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard/events")
@CrossOrigin(origins = "http://localhost:3000")  // Allow frontend access
public class DashboardEventController {

    private final DashboardEventService service;

    public DashboardEventController(DashboardEventService service) {
        this.service = service;
    }
@GetMapping
    public List<DashboardEvent> getEvents() {
        return service.getAllEvents();
    }
    // ✅ NEW: Endpoint to get events for ONLY a specific priest
    @GetMapping("/priest/{priestId}")
    public List<DashboardEvent> getEventsByPriest(@PathVariable Long priestId) {
        return service.getEventsByPriestId(priestId);
    }
    
    // ✅ UPDATED: Creating an event now requires the priestId in the body
    @PostMapping
    public DashboardEvent createEvent(@RequestBody DashboardEvent event) {
        // The frontend will now send the priestId
        return service.createEvent(event);
    }

    // Your PUT and DELETE methods should also have security to check ownership
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        service.deleteEvent(id);
    }

    @PutMapping("/{id}")
    public DashboardEvent updateEvent(@PathVariable Long id, @RequestBody DashboardEvent updatedEvent) {
        return service.updateEvent(id, updatedEvent);
    }
}