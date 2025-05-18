package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/booking")
public class AppointmentBookingController {

    @Autowired
    private AppointmentBookingRepository appointmentBookingRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new appointment with default status = "PENDING"
    @PostMapping
    public AppointmentBooking createAppointment(@RequestBody AppointmentBookingDto dto) {
        // Validate priestId is provided
        if (dto.getPriestId() == null) {
            throw new RuntimeException("Priest ID is required");
        }

        // Find priest by ID
        User priest = userRepository.findById(dto.getPriestId())
                .orElseThrow(() -> new RuntimeException("Priest not found with id " + dto.getPriestId()));

        // Map DTO to entity
        AppointmentBooking appointment = AppointmentBooking.builder()
                .event(dto.getEvent())
                .name(dto.getName())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .note(dto.getNote())
                .date(dto.getDate())
                .start(dto.getStart())
                .end(dto.getEnd())
                .priest(priest)
                .status("PENDING")
                .build();

        return appointmentBookingRepository.save(appointment);
    }

    // Get all appointments
    @GetMapping("/all")
    public List<AppointmentBooking> getAllAppointments() {
        return appointmentBookingRepository.findAll();
    }

    // Get appointments by priest ID
    @GetMapping("/priest/{priestId}")
    public List<AppointmentBooking> getAppointmentsByPriest(@PathVariable Long priestId) {
        return appointmentBookingRepository.findByPriestId(priestId);
    }

    // Get only pending appointments
    @GetMapping("/pending")
    public List<AppointmentBooking> getPendingAppointments() {
        return appointmentBookingRepository.findByStatus("PENDING");
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
}
