package com.example.myapp.panchang;

import org.springframework.stereotype.Component;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LagnaCalculator {
    public record TimeRange(LocalTime start, LocalTime end) {}

    private static final String[] LAGNA_ORDER = { "Mesha", "Vrishabha", "Mithuna", "Karka", "Simha", "Kanya", "Tula", "Vrischika", "Dhanu", "Makara", "Kumbha", "Meena" };

    // This is a simplified model. A precise calculation is highly complex.
    // This assumes each Lagna has a fixed duration of roughly 2 hours.
    public Map<String, TimeRange> calculateLagnasForDay(LocalTime sunrise) {
        Map<String, TimeRange> lagnaTimings = new LinkedHashMap<>();
        LocalTime currentLagnaStart = sunrise;
        for (String lagnaName : LAGNA_ORDER) {
            LocalTime currentLagnaEnd = currentLagnaStart.plusHours(2).minusNanos(1);
            lagnaTimings.put(lagnaName, new TimeRange(currentLagnaStart, currentLagnaEnd));
            currentLagnaStart = currentLagnaStart.plusHours(2);
        }
        return lagnaTimings;
    }
}