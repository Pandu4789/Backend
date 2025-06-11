package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/muhurtam")
@CrossOrigin(origins = "http://localhost:3000")
public class MuhurtamRequestController {

    @Autowired
    private MuhurtamRequestRepository muhurtamRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    // Submit a new Muhurtam request using DTO
    @PostMapping("/request")
    public ResponseEntity<?> submitMuhurtamRequest( @RequestBody MuhurtamRequestDto dto) {
        if (dto.getPriestId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Priest ID is required"));
        }

        Optional<User> priestOpt = userRepository.findById(dto.getPriestId());
        Optional<User> userOpt = userRepository.findById(dto.getUserId());

        if (priestOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Priest not found with id " + dto.getPriestId()));
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found with id " + dto.getUserId()));
        }

        Event event = null;
        if (dto.getEvent() != null) {
            event = eventRepository.findById(dto.getEvent()).orElse(null);
            if (event == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Event not found with id " + dto.getEvent()));
            }
        }

        MuhurtamRequest request = MuhurtamRequest.builder()
                .event(event)
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .nakshatram(dto.getNakshatram())
                .date(dto.getDate())
                .time(dto.getTime())
                .place(dto.getPlace())
                .note(dto.getNote())
                .viewed(false)
                .priest(priestOpt.get())
                .user(userOpt.get())
                .build();

        MuhurtamRequest savedRequest = muhurtamRequestRepository.save(request);
        return ResponseEntity.ok(new MuhurtamRequestDto(savedRequest));
    }

    // Get all requests
    @GetMapping("/all")
    public ResponseEntity<?> getAllRequests() {
        try {
            List<MuhurtamRequestDto> response = muhurtamRequestRepository.findAll().stream()
                    .map(MuhurtamRequestDto::new)
                    .toList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Get requests by customer ID
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<MuhurtamRequestDto>> getRequestsByCustomer(@PathVariable("customerId") Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + userId));

        List<MuhurtamRequestDto> response = muhurtamRequestRepository.findByUserId(userId).stream()
                .map(MuhurtamRequestDto::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Get requests by priest ID
    @GetMapping("/priest/{priestId}")
    public ResponseEntity<List<MuhurtamRequestDto>> getRequestsByPriest(@PathVariable Long priestId) {
        List<MuhurtamRequestDto> response = muhurtamRequestRepository.findByPriestId(priestId).stream()
                .map(MuhurtamRequestDto::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Update viewed status (true/false)
    @PutMapping("/{id}/viewed")
    public ResponseEntity<String> updateViewedStatus(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        if (!payload.containsKey("viewed")) {
            return ResponseEntity.badRequest().body("Missing 'viewed' field");
        }

        boolean viewed = Boolean.parseBoolean(payload.get("viewed").toString());

        return muhurtamRequestRepository.findById(id)
                .map(request -> {
                    request.setViewed(viewed);
                    muhurtamRequestRepository.save(request);
                    return ResponseEntity.ok("Viewed status updated to " + viewed);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
