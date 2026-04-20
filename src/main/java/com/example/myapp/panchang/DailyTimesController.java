package com.example.myapp.panchang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/daily-times")
@CrossOrigin(origins = "http://localhost:3000")
public class DailyTimesController {

    @Autowired
    private DailyTimesRepository dailyTimesRepository;

    @Autowired
    private SunriseSunsetCalculator sunriseSunsetCalculator;

    @Autowired
    private LagnaCalculator lagnaCalculator;

    @Autowired
    private PanchangCalculator panchangCalculator; // 👈 NEW: Injected for dynamic Kalam calculation

    /**
     * UPDATED ENDPOINT: Calculates all daily timings (Lagnas, Rahu Kalam,
     * Yamagandam)
     * on-the-fly using dynamic coordinates and timezone.
     */
    @GetMapping("/calculate")
    public ResponseEntity<?> calculateAllDailyTimes(
            @RequestParam String date,
            @RequestParam(defaultValue = "32.7767") double lat,
            @RequestParam(defaultValue = "-96.7970") double lon,
            @RequestParam(defaultValue = "America/Chicago") String tz) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);

            // Step 1: Calculate Sunrise & Sunset using dynamic location
            SunriseSunsetCalculator.Timings sunTimings = sunriseSunsetCalculator.getSunriseSunset(parsedDate, lat, lon,
                    tz);

            if (sunTimings.getSunrise() == null || sunTimings.getSunset() == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Could not calculate sun timings for this location/date."));
            }

            LocalTime sunrise = sunTimings.getSunrise().toLocalTime();
            LocalTime sunset = sunTimings.getSunset().toLocalTime();

            // Step 2: Initialize the DailyTimes object
            DailyTimes calculatedTimes = new DailyTimes();
            calculatedTimes.setDate(parsedDate);

            // Step 3: DYNAMIC CALCULATION - Rahu Kalam & Yamagandam
            // These are now calculated based on the day length, not fetched from a DB
            Map<String, PanchangCalculator.TimeRange> kalams = panchangCalculator.calculateKalamPeriods(sunrise, sunset,
                    parsedDate.getDayOfWeek());

            calculatedTimes.setRahukalamStart(kalams.get("rahu").start);
            calculatedTimes.setRahukalamEnd(kalams.get("rahu").end);
            calculatedTimes.setYamagandamStart(kalams.get("yama").start);
            calculatedTimes.setYamagandamEnd(kalams.get("yama").end);

            // Step 4: Calculate Lagnas based on local sunrise
            // Note: sunSignToday can be made dynamic later based on date
            String sunSignToday = "Mithuna";
            Map<String, LagnaCalculator.TimeRange> lagnas = lagnaCalculator.calculateLagnasForDay(sunrise,
                    sunSignToday);
            populateLagnas(calculatedTimes, lagnas);

            // You can also add logic for Durmuhurtham or Varjyam here if calculators exist

            return ResponseEntity.ok(calculatedTimes);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to calculate timings.", "details", e.getMessage()));
        }
    }

    // --- CRUD METHODS (For saved records) ---

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
     * Helper to map calculated Lagna values into the DailyTimes entity fields.
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