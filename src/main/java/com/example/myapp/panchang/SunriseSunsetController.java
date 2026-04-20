package com.example.myapp.panchang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sun")
public class SunriseSunsetController {

    @Autowired
    private SunriseSunsetCalculator calculator;

    @GetMapping
    public ResponseEntity<?> getSunriseSunset(
            @RequestParam("date") String dateStr,
            @RequestParam(defaultValue = "32.7767") double lat,
            @RequestParam(defaultValue = "-96.7970") double lon,
            @RequestParam(defaultValue = "America/Chicago") String tz // 👈 Dynamic Timezone
    ) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

            // Pass the timezone string to the calculator
            SunriseSunsetCalculator.Timings timings = calculator.getSunriseSunset(date, lat, lon, tz);

            if (timings.getSunrise() == null || timings.getSunset() == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Sunrise or sunset does not occur on this day at this location."));
            }

            Map<String, String> result = new HashMap<>();
            // toLocalTime() now automatically reflects the user's specific timezone
            result.put("sunrise", timings.getSunrise().toLocalTime().toString());
            result.put("sunset", timings.getSunset().toLocalTime().toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid parameters: " + e.getMessage()));
        }
    }
}