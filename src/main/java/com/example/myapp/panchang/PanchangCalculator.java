package com.example.myapp.panchang;

import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class PanchangCalculator {

    public static class TimeRange {
        public final LocalTime start;
        public final LocalTime end;

        public TimeRange(LocalTime s, LocalTime e) {
            this.start = s;
            this.end = e;
        }
    }

    public Map<String, TimeRange> calculateKalamPeriods(LocalTime sunrise, LocalTime sunset, DayOfWeek day) {
        long dayDurationMinutes = Duration.between(sunrise, sunset).toMinutes();
        double partLength = dayDurationMinutes / 8.0;

        Map<String, TimeRange> periods = new HashMap<>();

        // Rahu Kalam Mapping (Day -> Which of the 8 segments)
        // Mon:2, Tue:7, Wed:5, Thu:6, Fri:4, Sat:3, Sun:8
        int[] rahuParts = { 8, 2, 7, 5, 6, 4, 3 }; // Index 0 is Sunday
        int rahuPart = rahuParts[day.getValue() % 7];
        periods.put("rahu", calculateRange(sunrise, partLength, rahuPart));

        // Yamagandam Mapping
        // Mon:5, Tue:4, Wed:3, Thu:2, Fri:1, Sat:7, Sun:6
        int[] yamaParts = { 6, 5, 4, 3, 2, 1, 7 }; // Index 0 is Sunday
        int yamaPart = yamaParts[day.getValue() % 7];
        periods.put("yama", calculateRange(sunrise, partLength, yamaPart));

        return periods;
    }

    private TimeRange calculateRange(LocalTime sunrise, double partLength, int segment) {
        LocalTime start = sunrise.plusMinutes((long) ((segment - 1) * partLength));
        LocalTime end = sunrise.plusMinutes((long) (segment * partLength));
        return new TimeRange(start, end);
    }
}