package com.example.myapp.panchang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/muhurtam")
@CrossOrigin(origins = "http://localhost:3000")
public class MuhurtamFinderController {

    @Autowired
    private MuhurtamFinderService muhurtamFinderService;

    // ✅ UPDATED: Method now returns the new MuhurtamApiResponse object
    @GetMapping("/find")
    public ResponseEntity<MuhurtamApiResponse> findMuhurtamGet(@RequestParam List<String> nakshatrams) {
        // The service now returns the container object
        MuhurtamApiResponse response = muhurtamFinderService.findMuhurtams(nakshatrams);
        return ResponseEntity.ok(response);
    }

    // ✅ UPDATED: Method now returns the new MuhurtamApiResponse object
    @PostMapping("/find")
    public ResponseEntity<MuhurtamApiResponse> findMuhurtam(@RequestBody MuhurtamFindRequestDto request) {
        try {
            // The service now returns the container object, which holds both lists
            MuhurtamApiResponse response = muhurtamFinderService.findMuhurtams(request.getNakshatrams());
            
            // Optional: You can still log the size of the daily results if you want
            if (response != null && response.getDailyResults() != null) {
                System.out.println("Results size: " + response.getDailyResults().size());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
