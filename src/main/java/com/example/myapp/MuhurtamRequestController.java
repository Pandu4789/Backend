package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/muhurtam")
@CrossOrigin(origins = "http://localhost:3000")
public class MuhurtamRequestController {

    @Autowired
    private MuhurtamRequestRepository muhurtamRequestRepository;

    @Autowired
    private UserRepository userRepository;

    // Submit a new Muhurtam request using DTO
    @PostMapping("/request")
    public ResponseEntity<MuhurtamRequest> submitMuhurtamRequest(@RequestBody MuhurtamRequestDto dto) {
        if (dto.getPriestId() == null) {
            return ResponseEntity.badRequest().build();
        }

        User priest = userRepository.findById(dto.getPriestId())
                .orElseThrow(() -> new RuntimeException("Priest not found with id " + dto.getPriestId()));

        MuhurtamRequest request = MuhurtamRequest.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
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
    public ResponseEntity<List<MuhurtamRequest>> getAllRequests() {
        List<MuhurtamRequest> requests = muhurtamRequestRepository.findAll();
        return ResponseEntity.ok(requests);
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
}
