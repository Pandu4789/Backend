package com.example.myapp.panchang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Import ResponseEntity
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
    public ResponseEntity<?> getSunriseSunset( // Return type changed to ResponseEntity<?>
        @RequestParam("date") String dateStr,
        @RequestParam(defaultValue = "32.7767") double lat, // Dallas Latitude
        @RequestParam(defaultValue = "-96.7970") double lon  // Dallas Longitude
    ) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        SunriseSunsetCalculator.Timings timings = calculator.getSunriseSunset(date, lat, lon);

        // ✅ ADD THIS CHECK to handle cases with no sunrise/sunset
        if (timings.getSunrise() == null || timings.getSunset() == null) {
            // Return a 404 Not Found response with a clear error message
            return ResponseEntity.status(404)
                .body(Map.of("error", "Sunrise or sunset does not occur on this day at this location."));
        }

        // This code only runs if the timings are valid
        Map<String, String> result = new HashMap<>();
        result.put("sunrise", timings.getSunrise().toLocalTime().toString());
        result.put("sunset", timings.getSunset().toLocalTime().toString());
        
        return ResponseEntity.ok(result); // Return a 200 OK response
    }
}