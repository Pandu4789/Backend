package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:3000")
public class AppointmentController {

    @Autowired
    private AppointmentRepository repository;

    @Autowired
    private EventRepository eventRepository;

    @PostMapping("/priest/{priestId}")
    public Appointment saveAppointment(@PathVariable Long priestId, @RequestBody AppointmentDto dto) {
        // Parse date and time strings into LocalDate and LocalTime objects
        LocalDate datePart = LocalDate.parse(dto.getDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalTime startTimePart = LocalTime.parse(dto.getStart(), DateTimeFormatter.ISO_LOCAL_TIME);
        LocalTime endTimePart = LocalTime.parse(dto.getEnd(), DateTimeFormatter.ISO_LOCAL_TIME);

        // Combine date and time into LocalDateTime objects
        LocalDateTime startDateTime = LocalDateTime.of(datePart, startTimePart);
        LocalDateTime endDateTime = LocalDateTime.of(datePart, endTimePart);

        // Fetch Event entity by ID and handle not found
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + dto.getEventId()));

        // Create and populate Appointment entity
        Appointment appointment = new Appointment();
        appointment.setName(dto.getName());
        appointment.setPhone(dto.getPhone());
        appointment.setAddress(dto.getAddress());
        appointment.setEventName(event.getName()); // store event name, as expected by backend
        appointment.setNote(dto.getNote());
        appointment.setStartTime(startDateTime);
        appointment.setEnd(endDateTime);
        appointment.setPriestId(priestId);

        // Save and return the appointment entity
        return repository.save(appointment);
    }

    @GetMapping
    public List<Appointment> getAll() {
        return repository.findAll();
    }

    @GetMapping("/priest/{priestId}")
    public List<Appointment> getByPriestId(@PathVariable Long priestId) {
        return repository.findByPriestId(priestId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
