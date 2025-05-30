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

    @PostMapping("/priest/{priestId}")
public Appointment saveAppointment(@PathVariable Long priestId, @RequestBody Appointment appointment) {
    System.out.println("Received appointment data: " + appointment);
    appointment.setPriestId(priestId);  // Important: associate the appointment with the priest
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
