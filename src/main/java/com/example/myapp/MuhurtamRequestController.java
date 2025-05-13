package com.example.myapp;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/muhurtam")
@CrossOrigin(origins = "http://localhost:3000") // adjust if needed
public class MuhurtamRequestController {

    @Autowired
    private MuhurtamRequestRepository muhurtamRequestRepository;

    @PostMapping("/request")
    public ResponseEntity<MuhurtamRequest> submitMuhurtamRequest(@RequestBody MuhurtamRequest request) {
        // Default viewed status to false when saving a new request
        request.setViewed(false);
        MuhurtamRequest savedRequest = muhurtamRequestRepository.save(request);
        return ResponseEntity.ok(savedRequest);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MuhurtamRequest>> getAllRequests() {
        List<MuhurtamRequest> requests = muhurtamRequestRepository.findAll();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/view/{id}")
    public ResponseEntity<String> markAsViewed(@PathVariable Long id) {
        Optional<MuhurtamRequest> optionalRequest = muhurtamRequestRepository.findById(id);
        if (optionalRequest.isPresent()) {
            MuhurtamRequest request = optionalRequest.get();
            request.setViewed(true);
            muhurtamRequestRepository.save(request);
            return ResponseEntity.ok("Marked as viewed");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
