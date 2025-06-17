package com.example.myapp.panchang;

import org.springframework.stereotype.Component;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LagnaCalculator {
    public record TimeRange(LocalTime start, LocalTime end) {}

    // The order of Lagnas (zodiac signs) remains the same.
    private static final String[] LAGNA_ORDER = {
        "Mesha", "Vrishabha", "Mithuna", "Karka", "Simha", "Kanya",
        "Tula", "Vrischika", "Dhanu", "Makara", "Kumbha", "Meena"
    };

    /**
     * ✅ UPDATED: More accurate Lagna durations in minutes.
     * These are approximate rising times for a mid-northern latitude (~32° N, like Dallas).
     * Notice how they are not all 120 minutes (2 hours).
     */
    private static final Map<String, Integer> LAGNA_DURATIONS_MINUTES = new LinkedHashMap<>();
    static {
        LAGNA_DURATIONS_MINUTES.put("Mesha", 104);
        LAGNA_DURATIONS_MINUTES.put("Vrishabha", 111);
        LAGNA_DURATIONS_MINUTES.put("Mithuna", 125);
        LAGNA_DURATIONS_MINUTES.put("Karka", 136);
        LAGNA_DURATIONS_MINUTES.put("Simha", 138);
        LAGNA_DURATIONS_MINUTES.put("Kanya", 135);
        LAGNA_DURATIONS_MINUTES.put("Tula", 130);
        LAGNA_DURATIONS_MINUTES.put("Vrischika", 124);
        LAGNA_DURATIONS_MINUTES.put("Dhanu", 115);
        LAGNA_DURATIONS_MINUTES.put("Makara", 106);
        LAGNA_DURATIONS_MINUTES.put("Kumbha", 98);
        LAGNA_DURATIONS_MINUTES.put("Meena", 98);
    }

    /**
     * ✅ UPDATED: Calculates Lagna timings using specific durations for each sign.
     * This method is now significantly more accurate than the simple 2-hour approximation.
     * * @param sunrise The time of sunrise for the given day.
     * @param sunSign The zodiac sign the Sun is in on that day (e.g., "Karka"). This determines the starting Lagna.
     * @return A map of Lagna names to their start and end times.
     */
    public Map<String, TimeRange> calculateLagnasForDay(LocalTime sunrise, String sunSign) {
        Map<String, TimeRange> lagnaTimings = new LinkedHashMap<>();
        
        // Find the index of the starting Lagna (the sign the sun is in at sunrise)
        int startIndex = 0;
        for (int i = 0; i < LAGNA_ORDER.length; i++) {
            if (LAGNA_ORDER[i].equalsIgnoreCase(sunSign)) {
                startIndex = i;
                break;
            }
        }

        LocalTime currentLagnaStart = sunrise;

        // Calculate the 12 Lagnas for the next 24 hours, starting with the sun's sign.
        for (int i = 0; i < 12; i++) {
            // The loop wraps around the zodiac signs
            String lagnaName = LAGNA_ORDER[(startIndex + i) % 12];
            
            // Get the specific duration for this Lagna from our map
            int durationInMinutes = LAGNA_DURATIONS_MINUTES.getOrDefault(lagnaName, 120); // Default to 120 if not found
            
            // Calculate the end time by adding the specific duration
            LocalTime currentLagnaEnd = currentLagnaStart.plusMinutes(durationInMinutes);
            
            lagnaTimings.put(lagnaName, new TimeRange(currentLagnaStart, currentLagnaEnd));
            
            // The start of the next Lagna is the end of the current one
            currentLagnaStart = currentLagnaEnd;
        }
        
        return lagnaTimings;
    }
}