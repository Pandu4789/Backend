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

    @PostMapping
    public DashboardEvent createEvent(@RequestBody DashboardEvent event) {
        return service.createEvent(event);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        service.deleteEvent(id);
    }

    @PutMapping("/{id}")
    public DashboardEvent updateEvent(@PathVariable Long id, @RequestBody DashboardEvent updatedEvent) {
        return service.updateEvent(id, updatedEvent);
    }
}
