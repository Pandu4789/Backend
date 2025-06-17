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

    // ✅ NEW: Inject the calculators we need
    @Autowired
    private SunriseSunsetCalculator sunriseSunsetCalculator;

    @Autowired
    private LagnaCalculator lagnaCalculator;

    /**
     * ✅ NEW ENDPOINT: Calculates all daily timings for a given date on-the-fly.
     * This endpoint uses the calculators to generate a complete DailyTimes object
     * without needing it to be saved in the database first.
     *
     * How to test in Postman:
     * GET http://localhost:8080/api/daily-times/calculate?date=2025-06-16
     */
    @GetMapping("/calculate")
    public ResponseEntity<?> calculateAllDailyTimes(@RequestParam String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);

            // Step 1: Calculate Sunrise to use as input for the Lagna calculation.
            SunriseSunsetCalculator.Timings sunTimings = sunriseSunsetCalculator.getSunriseSunset(parsedDate, 32.7767, -96.7970);
            if (sunTimings.getSunrise() == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Could not calculate sunrise for this date."));
            }
            LocalTime sunrise = sunTimings.getSunrise().toLocalTime();

            // Step 2: Determine the Sun's zodiac sign for the day.
            // NOTE: For a full implementation, you would get this from your Panchangam data.
            // For this example, we will hardcode it. For mid-June, the Sun is in Mithuna (Gemini).
            String sunSignToday = "Mithuna";

            // Step 3: Calculate the Lagna timings.
            Map<String, LagnaCalculator.TimeRange> lagnas = lagnaCalculator.calculateLagnasForDay(sunrise, sunSignToday);

            // Step 4: Create a new DailyTimes object and populate it with the calculated data.
            DailyTimes calculatedTimes = new DailyTimes();
            calculatedTimes.setDate(parsedDate);
            
            // This is a helper method to map the Lagna results to the entity fields.
            populateLagnas(calculatedTimes, lagnas);

            // You could also populate Rahu Kalam, etc., here if you had calculators for them.
            // For now, they will be null as they are not calculated by this endpoint.
            
            return ResponseEntity.ok(calculatedTimes);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to calculate timings.", "details", e.getMessage()));
        }
    }

    // --- YOUR EXISTING CRUD METHODS ---

    @GetMapping
    public List<DailyTimes> getAll() {
        return dailyTimesRepository.findAll();
    }

    @GetMapping("/by-date/{date}")
    public ResponseEntity<?> getDailyTimesByDate(@PathVariable String date) {
        // This method remains unchanged. It fetches SAVED data from the DB.
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
        // This method remains unchanged. It SAVES data to the DB.
        try {
            Optional<DailyTimes> existing = dailyTimesRepository.findByDate(dailyTimes.getDate());
            if (existing.isPresent()) {
                return ResponseEntity.status(409).body(Map.of("error", "Record already exists for date: " + dailyTimes.getDate()));
            }
            DailyTimes saved = dailyTimesRepository.save(dailyTimes);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Could not save record.", "details", e.getMessage()));
        }
    }
    
    // (PUT and DELETE methods remain unchanged)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDailyTimes(@PathVariable Long id, @RequestBody DailyTimes updatedData) { // ... unchanged
        return ResponseEntity.ok().build(); // Placeholder
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDailyTimes(@PathVariable Long id) { // ... unchanged
        return ResponseEntity.ok().build(); // Placeholder
    }


    /**
     * Helper method to transfer calculated Lagna timings from the Map
     * into the fields of the DailyTimes entity.
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