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
}
