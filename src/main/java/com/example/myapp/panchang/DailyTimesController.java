package com.example.myapp.panchang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/daily-times")
public class DailyTimesController {

    @Autowired
    private DailyTimesRepository dailyTimesRepository;

    // GET all
    @GetMapping
    public List<DailyTimes> getAll() {
        return dailyTimesRepository.findAll();
    }

    // GET by date
    @GetMapping("/by-date/{date}")
    public ResponseEntity<?> getDailyTimesByDate(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            Optional<DailyTimes> dailyTimesOpt = dailyTimesRepository.findByDate(parsedDate);

            if (dailyTimesOpt.isPresent()) {
                return ResponseEntity.ok(dailyTimesOpt.get());
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "Not found for date: " + date));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format: " + date));
        }
    }

    // POST (Create)
    @PostMapping
    public ResponseEntity<?> createDailyTimes(@RequestBody DailyTimes dailyTimes) {
        try {
            Optional<DailyTimes> existing = dailyTimesRepository.findByDate(dailyTimes.getDate());
            if (existing.isPresent()) {
                return ResponseEntity.status(409).body(Map.of("error", "Record already exists for date: " + dailyTimes.getDate()));
            }
            DailyTimes saved = dailyTimesRepository.save(dailyTimes);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Could not save record."));
        }
    }

    // PUT (Update)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDailyTimes(@PathVariable Long id, @RequestBody DailyTimes updatedData) {
        Optional<DailyTimes> existingOpt = dailyTimesRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Not found with ID: " + id));
        }

        DailyTimes existing = existingOpt.get();
        existing.setDate(updatedData.getDate());
        existing.setRahukalamStart(updatedData.getRahukalamStart());
        existing.setRahukalamEnd(updatedData.getRahukalamEnd());
        existing.setYamagandamStart(updatedData.getYamagandamStart());
        existing.setYamagandamEnd(updatedData.getYamagandamEnd());
        existing.setVarjamStart(updatedData.getVarjamStart());
        existing.setVarjamEnd(updatedData.getVarjamEnd());
        existing.setDurmohurtamStart(updatedData.getDurmohurtamStart());
        existing.setDurmohurtamEnd(updatedData.getDurmohurtamEnd());

        dailyTimesRepository.save(existing);
        return ResponseEntity.ok(existing);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDailyTimes(@PathVariable Long id) {
        Optional<DailyTimes> existing = dailyTimesRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Not found with ID: " + id));
        }
        dailyTimesRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }
}
