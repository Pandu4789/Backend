package com.example.myapp.panchang;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component // Make this class available for injection
public class SunriseSunsetCalculator {

    public static class Timings {
        private final ZonedDateTime sunrise;
        private final ZonedDateTime sunset;

        public Timings(ZonedDateTime sunrise, ZonedDateTime sunset) {
            this.sunrise = sunrise;
            this.sunset = sunset;
        }

        public ZonedDateTime getSunrise() { return sunrise; }
        public ZonedDateTime getSunset() { return sunset; }
    }

    public Timings getSunriseSunset(LocalDate date, double latitude, double longitude) {
        // --- Standard Astronomical Algorithm for Sunrise/Sunset ---

        // 1. Calculate the day of the year
        int dayOfYear = date.getDayOfYear();

        // 2. Convert latitude to radians
        double latRad = Math.toRadians(latitude);

        // 3. Calculate solar declination
        double declinationAngle = 0.409 * Math.sin(Math.toRadians(360.0 / 365.0) * (dayOfYear - 81));

        // 4. Calculate hour angle
        double hourAngle = Math.acos(
            (Math.sin(Math.toRadians(-0.83)) - Math.sin(latRad) * Math.sin(declinationAngle)) /
            (Math.cos(latRad) * Math.cos(declinationAngle))
        );

        // 5. Calculate sunrise and sunset times in UTC
        double sunriseUTC = 12 - (Math.toDegrees(hourAngle) / 15.0) - getEquationOfTime(dayOfYear) - (longitude / 15.0);
        double sunsetUTC = 12 + (Math.toDegrees(hourAngle) / 15.0) - getEquationOfTime(dayOfYear) - (longitude / 15.0);

        // 6. Convert UTC decimal hours to ZonedDateTime for the specific location
        ZoneId zoneId = ZoneId.of("America/Chicago"); // Timezone for Dallas

        ZonedDateTime sunriseTime = convertDecimalHoursToZonedDateTime(sunriseUTC, date, zoneId);
        ZonedDateTime sunsetTime = convertDecimalHoursToZonedDateTime(sunsetUTC, date, zoneId);

        return new Timings(sunriseTime, sunsetTime);
    }

    // Helper method to calculate the "Equation of Time"
    private double getEquationOfTime(int dayOfYear) {
        double b = Math.toRadians(360.0 / 365.0 * (dayOfYear - 81));
        return 9.87 * Math.sin(2 * b) - 7.53 * Math.cos(b) - 1.5 * Math.sin(b);
    }

    // Helper method to convert decimal hours to a proper ZonedDateTime
    private ZonedDateTime convertDecimalHoursToZonedDateTime(double decimalHours, LocalDate date, ZoneId zoneId) {
        int hours = (int) decimalHours;
        int minutes = (int) ((decimalHours - hours) * 60);
        int seconds = (int) ((((decimalHours - hours) * 60) - minutes) * 60);
        
        // Create as UTC first, then convert to the local timezone
        ZonedDateTime utcTime = ZonedDateTime.of(date, LocalTime.of(hours, minutes, seconds), ZoneId.of("UTC"));
        return utcTime.withZoneSameInstant(zoneId);
    }
}