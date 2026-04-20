package com.example.myapp.panchang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/daily-times")
@CrossOrigin(origins = "http://localhost:3000") // Enable CORS for your React app
public class DailyTimesController {

    @Autowired
    private DailyTimesRepository dailyTimesRepository;

    @Autowired
    private SunriseSunsetCalculator sunriseSunsetCalculator;

    @Autowired
    private LagnaCalculator lagnaCalculator;

    /**
     * UPDATED ENDPOINT: Now calculates all daily timings on-the-fly using dynamic
     * coordinates.
     * * How to test in Postman:
     * GET
     * http://localhost:8080/api/daily-times/calculate?date=2026-04-20&lat=36.1627&lon=-86.7816&tz=America/Chicago
     */
    @GetMapping("/calculate")
    public ResponseEntity<?> calculateAllDailyTimes(
            @RequestParam String date,
            @RequestParam(defaultValue = "32.7767") double lat, // Default: Dallas
            @RequestParam(defaultValue = "-96.7970") double lon, // Default: Dallas
            @RequestParam(defaultValue = "America/Chicago") String tz // Default: Chicago/Dallas TZ
    ) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);

            // Step 1: Calculate Sunrise using the dynamic location and timezone provided.
            SunriseSunsetCalculator.Timings sunTimings = sunriseSunsetCalculator.getSunriseSunset(parsedDate, lat, lon,
                    tz);

            if (sunTimings.getSunrise() == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Could not calculate sunrise for this location/date."));
            }

            // toLocalTime() ensures the sunrise is in the user's local clock context.
            LocalTime sunrise = sunTimings.getSunrise().toLocalTime();

            // Step 2: Determine the Sun's zodiac sign.
            // For now, this is hardcoded. In a full version, this would change based on the
            // date.
            String sunSignToday = "Mithuna";

            // Step 3: Calculate the Lagna timings relative to this specific local sunrise.
            Map<String, LagnaCalculator.TimeRange> lagnas = lagnaCalculator.calculateLagnasForDay(sunrise,
                    sunSignToday);

            // Step 4: Map the results to the DailyTimes object.
            DailyTimes calculatedTimes = new DailyTimes();
            calculatedTimes.setDate(parsedDate);
            populateLagnas(calculatedTimes, lagnas);

            return ResponseEntity.ok(calculatedTimes);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to calculate timings.", "details", e.getMessage()));
        }
    }

    // --- CRUD METHODS ---

    @GetMapping
    public List<DailyTimes> getAll() {
        return dailyTimesRepository.findAll();
    }

    @GetMapping("/by-date/{date}")
    public ResponseEntity<?> getDailyTimesByDate(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            Optional<DailyTimes> dailyTimesOpt = dailyTimesRepository.findByDate(parsedDate);
            return dailyTimesOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "Not found for date: " + date)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format: " + date));
        }
    }

    @PostMapping
    public ResponseEntity<?> createDailyTimes(@RequestBody DailyTimes dailyTimes) {
        try {
            Optional<DailyTimes> existing = dailyTimesRepository.findByDate(dailyTimes.getDate());
            if (existing.isPresent()) {
                return ResponseEntity.status(409)
                        .body(Map.of("error", "Record already exists for date: " + dailyTimes.getDate()));
            }
            DailyTimes saved = dailyTimesRepository.save(dailyTimes);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Could not save record.", "details", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDailyTimes(@PathVariable Long id, @RequestBody DailyTimes updatedData) {
        return dailyTimesRepository.findById(id)
                .map(existing -> {
                    updatedData.setId(id);
                    return ResponseEntity.ok(dailyTimesRepository.save(updatedData));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDailyTimes(@PathVariable Long id) {
        if (dailyTimesRepository.existsById(id)) {
            dailyTimesRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Transfers calculated Lagna timings from the Map into the DailyTimes entity
     * fields.
     */
    private void populateLagnas(DailyTimes dailyTimes, Map<String, LagnaCalculator.TimeRange> lagnas) {
        dailyTimes.setMeshaLagnaStart(lagnas.get("Mesha").start());
        dailyTimes.setMeshaLagnaEnd(lagnas.get("Mesha").end());
        dailyTimes.setVrishabhaLagnaStart(lagnas.get("Vrishabha").start());
        dailyTimes.setVrishabhaLagnaEnd(lagnas.get("Vrishabha").end());
        dailyTimes.setMithunaLagnaStart(lagnas.get("Mithuna").start());
        dailyTimes.setMithunaLagnaEnd(lagnas.get("Mithuna").end());
        dailyTimes.setKarkaLagnaStart(lagnas.get("Karka").start());
        dailyTimes.setKarkaLagnaEnd(lagnas.get("Karka").end());
        dailyTimes.setSimhaLagnaStart(lagnas.get("Simha").start());
        dailyTimes.setSimhaLagnaEnd(lagnas.get("Simha").end());
        dailyTimes.setKanyaLagnaStart(lagnas.get("Kanya").start());
        dailyTimes.setKanyaLagnaEnd(lagnas.get("Kanya").end());
        dailyTimes.setTulaLagnaStart(lagnas.get("Tula").start());
        dailyTimes.setTulaLagnaEnd(lagnas.get("Tula").end());
        dailyTimes.setVrischikaLagnaStart(lagnas.get("Vrischika").start());
        dailyTimes.setVrischikaLagnaEnd(lagnas.get("Vrischika").end());
        dailyTimes.setDhanuLagnaStart(lagnas.get("Dhanu").start());
        dailyTimes.setDhanuLagnaEnd(lagnas.get("Dhanu").end());
        dailyTimes.setMakaraLagnaStart(lagnas.get("Makara").start());
        dailyTimes.setMakaraLagnaEnd(lagnas.get("Makara").end());
        dailyTimes.setKumbhaLagnaStart(lagnas.get("Kumbha").start());
        dailyTimes.setKumbhaLagnaEnd(lagnas.get("Kumbha").end());
        dailyTimes.setMeenaLagnaStart(lagnas.get("Meena").start());
        dailyTimes.setMeenaLagnaEnd(lagnas.get("Meena").end());
    }
}