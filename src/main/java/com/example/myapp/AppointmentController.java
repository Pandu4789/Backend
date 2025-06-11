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

    @PostMapping("/priest/{priestId}")
    public Appointment saveAppointment(@PathVariable Long priestId, @RequestBody AppointmentDto dto) {
        // Combine the date and time strings into LocalDateTime objects
        LocalDate datePart = LocalDate.parse(dto.getDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalTime startTimePart = LocalTime.parse(dto.getStart(), DateTimeFormatter.ISO_LOCAL_TIME);
        LocalTime endTimePart = LocalTime.parse(dto.getEnd(), DateTimeFormatter.ISO_LOCAL_TIME);

        LocalDateTime startDateTime = LocalDateTime.of(datePart, startTimePart);
        LocalDateTime endDateTime = LocalDateTime.of(datePart, endTimePart);

        Appointment appointment = new Appointment();
        appointment.setName(dto.getName());
        appointment.setPhone(dto.getPhone());
        appointment.setAddress(dto.getAddress());
        appointment.setEvent(dto.getEvents());
        appointment.setNote(dto.getNote());
        appointment.setStartTime(startDateTime); // Set the LocalDateTime object
        appointment.setEnd(endDateTime);     // Set the LocalDateTime object
        appointment.setPriestId(priestId);

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
