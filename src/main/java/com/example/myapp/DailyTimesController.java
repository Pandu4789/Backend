package com.example.myapp;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/daily-times")
public class DailyTimesController {

    @Autowired
    private DailyTimesRepository dailyTimesRepository;

    @GetMapping("/by-date/{date}")
    public ResponseEntity<?> getDailyTimesByDate(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            Optional<DailyTimes> dailyTimesOpt = dailyTimesRepository.findByDate(parsedDate);

            if (dailyTimesOpt.isPresent()) {
                return ResponseEntity.ok(dailyTimesOpt.get());
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "DailyTimes not found for date: " + date);
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid date format: " + date);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
