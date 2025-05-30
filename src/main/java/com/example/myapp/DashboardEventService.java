package com.example.myapp;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardEventService {

    private final DashboardEventRepository repository;

    public DashboardEventService(DashboardEventRepository repository) {
        this.repository = repository;
    }

    public List<DashboardEvent> getAllEvents() {
        return repository.findAll();
    }

    public DashboardEvent createEvent(DashboardEvent event) {
        return repository.save(event);
    }

    public void deleteEvent(Long id) {
        repository.deleteById(id);
    }

    public DashboardEvent updateEvent(Long id, DashboardEvent updatedEvent) {
        return repository.findById(id)
            .map(event -> {
                event.setTitle(updatedEvent.getTitle());
                event.setPhotoUrl(updatedEvent.getPhotoUrl());
                event.setDescription(updatedEvent.getDescription());
                return repository.save(event);
            })
            .orElseThrow(() -> new RuntimeException("Event not found with id " + id));
    }
}
