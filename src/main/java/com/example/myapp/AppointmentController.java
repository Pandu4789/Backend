package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:3000")


public class AppointmentController {

    @Autowired
    private AppointmentRepository repository;

    @PostMapping
    public Appointment saveAppointment(@RequestBody Appointment appointment) {
        System.out.println("Received appointment data: " + appointment);
        return repository.save(appointment);
    }
    

    @GetMapping
    public List<Appointment> getAll() {
        return repository.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
