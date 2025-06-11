package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    // GET all events
    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // GET a single event by ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE a new event
    @PostMapping
    public Event createEvent(@RequestBody EventDto eventDto) {
        Event event = Event.builder()
                .name(eventDto.getName())
                .description(eventDto.getDescription())
                .category(eventDto.getCategory())
                .duration(eventDto.getDuration())
                .estimatedPrice(eventDto.getEstimatedPrice())
                .build();
        return eventRepository.save(event);
    }

    // UPDATE an existing event
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody EventDto eventDetails) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setName(eventDetails.getName());
                    event.setDescription(eventDetails.getDescription());
                    event.setCategory(eventDetails.getCategory());
                    event.setDuration(eventDetails.getDuration());
                    event.setEstimatedPrice(eventDetails.getEstimatedPrice());
                    return ResponseEntity.ok(eventRepository.save(event));
                }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE an event
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(event -> {
                    eventRepository.delete(event);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}