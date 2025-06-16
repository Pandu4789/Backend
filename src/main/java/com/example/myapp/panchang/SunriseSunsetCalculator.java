package com.example.myapp.panchang;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class SunriseSunsetCalculator {

    // The inner Timings class remains the same
    public static class Timings {
        private final ZonedDateTime sunrise;
        private final ZonedDateTime sunset;
        public Timings(ZonedDateTime s, ZonedDateTime ss) { this.sunrise = s; this.sunset = ss; }
        public ZonedDateTime getSunrise() { return sunrise; }
        public ZonedDateTime getSunset() { return sunset; }
    }

    public Timings getSunriseSunset(LocalDate date, double latitude, double longitude) {
        int dayOfYear = date.getDayOfYear();
        double latRad = Math.toRadians(latitude);
        double declinationAngle = 0.409 * Math.sin(Math.toRadians(360.0 / 365.0) * (dayOfYear - 81));

        double cosHourAngle = (Math.sin(Math.toRadians(-0.83)) - Math.sin(latRad) * Math.sin(declinationAngle)) /
                              (Math.cos(latRad) * Math.cos(declinationAngle));

        if (cosHourAngle > 1.0 || cosHourAngle < -1.0) {
            return new Timings(null, null);
        }
        
        double hourAngle = Math.acos(cosHourAngle);

        // This is the main calculation for the time in UTC decimal hours.
        // It's based on a standard formula where noon is 12.
        double sunriseUTC = 12 
            - (Math.toDegrees(hourAngle) / 15.0) 
            - (getEquationOfTime(dayOfYear) / 60.0) // ✅ FIX: Convert EoT from minutes to hours
            - (longitude / 15.0);
        
        double sunsetUTC = 12 
            + (Math.toDegrees(hourAngle) / 15.0) 
            - (getEquationOfTime(dayOfYear) / 60.0) // ✅ FIX: Convert EoT from minutes to hours
            - (longitude / 15.0);

        // The helper method now correctly converts the calculated UTC time to the local time zone.
        // Java's ZonedDateTime automatically handles whether to apply CDT (UTC-5) or CST (UTC-6).
        ZoneId zoneId = ZoneId.of("America/Chicago");
        ZonedDateTime sunriseTime = convertDecimalHoursToZonedDateTime(sunriseUTC, date, zoneId);
        ZonedDateTime sunsetTime = convertDecimalHoursToZonedDateTime(sunsetUTC, date, zoneId);

        return new Timings(sunriseTime, sunsetTime);
    }
    
    /**
     * This helper method calculates the Equation of Time in MINUTES.
     * It remains unchanged.
     */
    private double getEquationOfTime(int dayOfYear) {
        double b = Math.toRadians(360.0 / 365.0 * (dayOfYear - 81));
        return 9.87 * Math.sin(2 * b) - 7.53 * Math.cos(b) - 1.5 * Math.sin(b);
    }

    /**
     * This robust helper method converts decimal hours into a proper ZonedDateTime.
     * It remains unchanged from the last version.
     */
    private ZonedDateTime convertDecimalHoursToZonedDateTime(double decimalHours, LocalDate date, ZoneId zoneId) {
        long totalSeconds = (long) (decimalHours * 3600);
        ZonedDateTime startOfDayUTC = date.atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime preciseTimeUTC = startOfDayUTC.plusSeconds(totalSeconds);
        return preciseTimeUTC.withZoneSameInstant(zoneId);
    }
}