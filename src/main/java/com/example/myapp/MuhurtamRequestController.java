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
public ResponseEntity<?> submitMuhurtamRequest(@RequestBody MuhurtamRequestDto dto) {
    if (dto.getPriestId() == null) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Priest ID is required"));
    }

    User priest = userRepository.findById(dto.getPriestId())
            .orElse(null);

    if (priest == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Priest not found with id " + dto.getPriestId()));
    }

    // Assuming you have an EventRepository to fetch Event by ID
    Event event = null;
    if (dto.getEvent() != null) {
        // Replace eventRepository with your actual EventRepository bean
        event = eventRepository.findById(dto.getEvent())
                .orElse(null);
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
            .priest(priest)
            .build();

    MuhurtamRequest savedRequest = muhurtamRequestRepository.save(request);
    return ResponseEntity.ok(savedRequest);
}


    // Get all requests
    @GetMapping("/all")
public ResponseEntity<?> getAllRequests() {
    try {
        List<MuhurtamRequest> requests = muhurtamRequestRepository.findAll();
        List<MuhurtamRequestDto> response = requests.stream()
                .map(MuhurtamRequestDto::new)
                .toList();
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        e.printStackTrace(); // Logs to console
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
}



    // Get requests by priest ID
    @GetMapping("/priest/{priestId}")
    public ResponseEntity<List<MuhurtamRequest>> getRequestsByPriest(@PathVariable Long priestId) {
        List<MuhurtamRequest> requests = muhurtamRequestRepository.findByPriestId(priestId);
        return ResponseEntity.ok(requests);
    }

    // Mark request as viewed
    @PostMapping("/view/{id}")
    public ResponseEntity<String> markAsViewed(@PathVariable Long id) {
        return muhurtamRequestRepository.findById(id)
                .map(request -> {
                    request.setViewed(true);
                    muhurtamRequestRepository.save(request);
                    return ResponseEntity.ok("Marked as viewed");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
   @PutMapping("/viewed/{id}")
public ResponseEntity<String> updateViewedStatus(@PathVariable Long id, @RequestBody Map<String, Object> payload){
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
