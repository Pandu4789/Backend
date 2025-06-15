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
    @GetMapping("/find")
public ResponseEntity<List<MuhurtamFindResponseDto>> findMuhurtamGet(@RequestParam List<String> nakshatrams) {
    List<MuhurtamFindResponseDto> results = muhurtamFinderService.findMuhurtams(nakshatrams);
    return ResponseEntity.ok(results);
}

  @PostMapping("/find")
public ResponseEntity<List<MuhurtamFindResponseDto>> findMuhurtam(@RequestBody MuhurtamFindRequestDto request) {
    try {
        // System.out.println("Incoming request: " + request);
        List<MuhurtamFindResponseDto> results = muhurtamFinderService.findMuhurtams(request.getNakshatrams());
        System.out.println("Results size: " + results.size());
        
        return ResponseEntity.ok(results);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().build();
    }
}

}