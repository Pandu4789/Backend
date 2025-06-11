package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/booking")
public class AppointmentBookingController {

    @Autowired
    private AppointmentBookingRepository appointmentBookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    // ✅ Create a new appointment with default status "PENDING"
    @PostMapping
    public AppointmentBooking createAppointment(@RequestBody AppointmentBookingDto dto) {
        if (dto.getPriestId() == null) {
            throw new RuntimeException("Priest ID is required");
        }

        User priest = userRepository.findById(dto.getPriestId())
                .orElseThrow(() -> new RuntimeException("Priest not found with ID: " + dto.getPriestId()));

        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + dto.getEventId()));

        AppointmentBooking appointment = AppointmentBooking.builder()
                .event(event)
                .name(dto.getName())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .note(dto.getNote())
                .date(dto.getDate())
                .start(dto.getStart())
                .end(dto.getEnd())
                .priest(priest)
                .user(dto.getUserId() != null ? userRepository.findById(dto.getUserId()).orElse(null) : null)
                .status("PENDING")
                .build();

        return appointmentBookingRepository.save(appointment);
    }

    // ✅ Get all appointments with priest info
    @GetMapping("/all")
    public List<AppointmentBookingResponseDto> getAllAppointments() {
        List<AppointmentBooking> appointments = appointmentBookingRepository.findAll();

        return appointments.stream().map(appointment -> {
            User priest = appointment.getPriest();
            User user = appointment.getUser();
            return new AppointmentBookingResponseDto(
                    appointment.getId(),
                    appointment.getEvent() != null ? appointment.getEvent().getId() : null,
                    appointment.getName(),
                    appointment.getPhone(),
                    appointment.getAddress(),
                    appointment.getNote(),
                    appointment.getDate(),
                    appointment.getStart(),
                    appointment.getEnd(),
                    appointment.getStatus(),
                    priest != null ? priest.getId() : null,
                    priest != null ? priest.getFirstName() + " " + priest.getLastName() : "Unknown",
                    user != null ? user.getId() : null);
        }).toList();
    }

    // ✅ Get appointments by priest ID
    @GetMapping("/priest/{priestId}")
    public List<AppointmentBooking> getAppointmentsByPriest(@PathVariable Long priestId) {
        return appointmentBookingRepository.findByPriestId(priestId);
    }

// ✅ Get appointments by customer (user) ID
@GetMapping("/customer/{customerId}")
public List<AppointmentBooking> getAppointmentsByCustomer(@PathVariable Long customerId) {
    return appointmentBookingRepository.findByUserId(customerId);
}

    // ✅ Get only pending appointments
    @GetMapping("/pending")
    public List<AppointmentBooking> getPendingAppointments() {
        return appointmentBookingRepository.findByStatus("PENDING");
    }

    // ✅ Accept an appointment
    @PutMapping("/accept/{id}")
    public AppointmentBooking acceptAppointment(@PathVariable Long id) {
        return updateStatusInternal(id, "ACCEPTED");
    }

    // ✅ Reject an appointment
    @PutMapping("/reject/{id}")
    public AppointmentBooking rejectAppointment(@PathVariable Long id) {
        return updateStatusInternal(id, "REJECTED");
    }

    // ✅ Update appointment details
    @PutMapping("/{id}")
    public AppointmentBooking updateAppointment(@PathVariable Long id, @RequestBody AppointmentBookingDto dto) {
        AppointmentBooking appointment = appointmentBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));

        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + dto.getEventId()));

        appointment.setEvent(event);
        appointment.setName(dto.getName());
        appointment.setPhone(dto.getPhone());
        appointment.setAddress(dto.getAddress());
        appointment.setNote(dto.getNote());
        appointment.setDate(dto.getDate());
        appointment.setStart(dto.getStart());
        appointment.setEnd(dto.getEnd());

        return appointmentBookingRepository.save(appointment);
    }

    // ✅ Delete an appointment
    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        appointmentBookingRepository.deleteById(id);
    }

    // ✅ Update status (custom status handler)
    @PutMapping("/{id}/status")
    public AppointmentBooking updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) {
            throw new RuntimeException("Status is required");
        }
        return updateStatusInternal(id, status);
    }

    // ✅ Utility method to update status
    private AppointmentBooking updateStatusInternal(Long id, String status) {
        AppointmentBooking appointment = appointmentBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
        appointment.setStatus(status);
        return appointmentBookingRepository.save(appointment);
    }
}
