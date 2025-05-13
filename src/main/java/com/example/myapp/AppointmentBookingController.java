package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000") // Adjust as needed
@RestController
@RequestMapping("/api/booking")
public class AppointmentBookingController {

    @Autowired
    private AppointmentBookingRepository appointmentBookingRepository;

    // Create a new appointment with default status = "PENDING"
    @PostMapping
    public AppointmentBooking createAppointment(@RequestBody AppointmentBooking appointment) {
        appointment.setStatus("PENDING");
        return appointmentBookingRepository.save(appointment);
    }

    // Get all appointments (any status)
    @GetMapping("/all")
    public List<AppointmentBooking> getAllAppointments() {
        return appointmentBookingRepository.findAll();
    }

    // Accept an appointment
    @PutMapping("/accept/{id}")
    public AppointmentBooking acceptAppointment(@PathVariable Long id) {
        AppointmentBooking appointment = appointmentBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
        appointment.setStatus("ACCEPTED");
        return appointmentBookingRepository.save(appointment);
    }

    // Reject an appointment
    @PutMapping("/reject/{id}")
    public AppointmentBooking rejectAppointment(@PathVariable Long id) {
        AppointmentBooking appointment = appointmentBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
        appointment.setStatus("REJECTED");
        return appointmentBookingRepository.save(appointment);
    }

    // Optional: Get only pending appointments if needed
    @GetMapping("/pending")
    public List<AppointmentBooking> getPendingAppointments() {
        return appointmentBookingRepository.findByStatus("PENDING");
    }
}
