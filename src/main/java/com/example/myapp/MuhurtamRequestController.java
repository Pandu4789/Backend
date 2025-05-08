package com.example.myapp;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/muhurtam")
@CrossOrigin(origins = "http://localhost:3000") // adjust for your frontend port
public class MuhurtamRequestController {

    @Autowired
    private MuhurtamRequestRepository muhurtamRequestRepository;

    @PostMapping("/request")
    public ResponseEntity<MuhurtamRequest> submitMuhurtamRequest(@RequestBody MuhurtamRequest request) {
        // save the incoming request (nakshatram can be null or empty)
        MuhurtamRequest savedRequest = muhurtamRequestRepository.save(request);
        return ResponseEntity.ok(savedRequest);
    }
    @GetMapping("/all")
public ResponseEntity<List<MuhurtamRequest>> getAllRequests() {
    List<MuhurtamRequest> requests = muhurtamRequestRepository.findAll();
    return ResponseEntity.ok(requests);
}

}
