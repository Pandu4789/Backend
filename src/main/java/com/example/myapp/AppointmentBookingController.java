package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping
    public AppointmentBookingResponseDto createAppointment(@RequestBody AppointmentBookingDto dto) {
        if (dto.getPriestId() == null) {
            throw new RuntimeException("Priest ID is required");
        }
        User priest = userRepository.findById(dto.getPriestId())
                .orElseThrow(() -> new RuntimeException("Priest not found with ID: " + dto.getPriestId()));
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + dto.getEventId()));
        User user = dto.getUserId() != null ? userRepository.findById(dto.getUserId()).orElse(null) : null;

        AppointmentBooking appointment = AppointmentBooking.builder()
                .event(event)
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .note(dto.getNote())
                .date(dto.getDate())
                .start(dto.getStart())
                .end(dto.getEnd())
                .priest(priest)
                .user(user)
                .status("PENDING")
                .priestName(priest.getFirstName() + " " + priest.getLastName())
                .build();
        
        AppointmentBooking savedAppointment = appointmentBookingRepository.save(appointment);
        return convertToDto(savedAppointment);
    }

    @GetMapping("/all")
    public List<AppointmentBookingResponseDto> getAllAppointments() {
        return appointmentBookingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/priest/{priestId}")
    public List<AppointmentBookingResponseDto> getAppointmentsByPriest(@PathVariable Long priestId) {
        return appointmentBookingRepository.findByPriestId(priestId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/customer/{customerId}")
    public List<AppointmentBookingResponseDto> getAppointmentsByCustomer(@PathVariable Long customerId) {
        return appointmentBookingRepository.findByUserId(customerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/accept/{id}")
    public AppointmentBookingResponseDto acceptAppointment(@PathVariable Long id) {
        AppointmentBooking updatedAppointment = updateStatusInternal(id, "ACCEPTED");
        return convertToDto(updatedAppointment);
    }

    @PutMapping("/reject/{id}")
    public AppointmentBookingResponseDto rejectAppointment(@PathVariable Long id) {
        AppointmentBooking updatedAppointment = updateStatusInternal(id, "REJECTED");
        return convertToDto(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        appointmentBookingRepository.deleteById(id);
    }

    private AppointmentBooking updateStatusInternal(Long id, String status) {
        AppointmentBooking appointment = appointmentBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
        appointment.setStatus(status);
        return appointmentBookingRepository.save(appointment);
    }

    // ✅ FIXED CONSTRUCTOR ARGUMENT ORDER
    private AppointmentBookingResponseDto convertToDto(AppointmentBooking appointment) {
        User priest = appointment.getPriest();
        User customer = appointment.getUser();
        
        return new AppointmentBookingResponseDto(
                appointment.getId(),                                  // 1. Long id
                appointment.getEvent() != null ? appointment.getEvent().getName() : null, // 2. String eventName
                appointment.getName(),                                // 3. String name
                appointment.getPhone(),                               // 4. String phone
                appointment.getEmail(),                               // 5. String email
                appointment.getAddress(),                             // 6. String address
                appointment.getNote(),                                // 7. String note
                appointment.getDate(),                                // 8. String date
                appointment.getStart(),                               // 9. String start
                appointment.getEnd(),                                 // 10. String end
                appointment.getStatus(),                              // 11. String status
                priest != null ? priest.getId() : null,               // 12. Long priestId
                appointment.getPriestName(),                          // 13. String priestName
                priest != null ? priest.getPhone() : "N/A",           // 14. String priestPhone
                priest != null ? priest.getEmail() : "N/A",           // 15. String priestEmail
                customer != null ? customer.getId() : null            // 16. Long userId
        );
    }
}