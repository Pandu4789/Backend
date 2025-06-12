package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // This tells Spring that this is a service class
public class DashboardEventService {

    private final DashboardEventRepository repository;

    @Autowired
    public DashboardEventService(DashboardEventRepository repository) {
        this.repository = repository;
    }

    // Method to get all events
    public List<DashboardEvent> getAllEvents() {
        return repository.findAll();
    }

    // Method to create a new event
    public DashboardEvent createEvent(DashboardEvent event) {
        // You can add validation logic here if needed
        return repository.save(event);
    }

    // Method to delete an event
    public void deleteEvent(Long id) {
        // Check if the event exists before trying to delete
        if (!repository.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        repository.deleteById(id);
    }

    // Method to update an event
    public DashboardEvent updateEvent(Long id, DashboardEvent updatedEventDetails) {
        // Find the existing event in the database
        DashboardEvent existingEvent = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        // Update the fields of the existing event with the new details
        existingEvent.setTitle(updatedEventDetails.getTitle());
        existingEvent.setDescription(updatedEventDetails.getDescription());
        existingEvent.setLocation(updatedEventDetails.getLocation());
        existingEvent.setDate(updatedEventDetails.getDate());
        existingEvent.setEventTime(updatedEventDetails.getEventTime());
        existingEvent.setImageUrl(updatedEventDetails.getImageUrl());

        // Save the updated event back to the database
        return repository.save(existingEvent);
    }
}