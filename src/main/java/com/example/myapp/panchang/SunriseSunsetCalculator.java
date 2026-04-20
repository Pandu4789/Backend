package com.example.myapp.panchang;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class SunriseSunsetCalculator {

    public static class Timings {
        private final ZonedDateTime sunrise;
        private final ZonedDateTime sunset;

        public Timings(ZonedDateTime s, ZonedDateTime ss) {
            this.sunrise = s;
            this.sunset = ss;
        }

        public ZonedDateTime getSunrise() {
            return sunrise;
        }

        public ZonedDateTime getSunset() {
            return sunset;
        }
    }

    /**
     * Calculates sunrise and sunset based on dynamic location and timezone.
     */
    public Timings getSunriseSunset(LocalDate date, double latitude, double longitude, String userTimeZone) {
        int dayOfYear = date.getDayOfYear();
        double latRad = Math.toRadians(latitude);

        // Basic solar declination calculation
        double declinationAngle = 0.409 * Math.sin(Math.toRadians(360.0 / 365.0) * (dayOfYear - 81));

        // Calculate the hour angle
        double cosHourAngle = (Math.sin(Math.toRadians(-0.83)) - Math.sin(latRad) * Math.sin(declinationAngle)) /
                (Math.cos(latRad) * Math.cos(declinationAngle));

        // Check for polar day/night
        if (cosHourAngle > 1.0 || cosHourAngle < -1.0) {
            return new Timings(null, null);
        }

        double hourAngle = Math.acos(cosHourAngle);

        // Calculate UTC decimal hours
        double sunriseUTC = 12
                - (Math.toDegrees(hourAngle) / 15.0)
                - (getEquationOfTime(dayOfYear) / 60.0)
                - (longitude / 15.0);

        double sunsetUTC = 12
                + (Math.toDegrees(hourAngle) / 15.0)
                - (getEquationOfTime(dayOfYear) / 60.0)
                - (longitude / 15.0);

        // 👈 DYNAMIC TIMEZONE CONVERSION
        // ZoneId.of converts strings like "Asia/Kolkata" or "Europe/London"
        // automatically
        ZoneId zoneId = ZoneId.of(userTimeZone);

        ZonedDateTime sunriseTime = convertDecimalHoursToZonedDateTime(sunriseUTC, date, zoneId);
        ZonedDateTime sunsetTime = convertDecimalHoursToZonedDateTime(sunsetUTC, date, zoneId);

        return new Timings(sunriseTime, sunsetTime);
    }

    private double getEquationOfTime(int dayOfYear) {
        double b = Math.toRadians(360.0 / 365.0 * (dayOfYear - 81));
        return 9.87 * Math.sin(2 * b) - 7.53 * Math.cos(b) - 1.5 * Math.sin(b);
    }

    private ZonedDateTime convertDecimalHoursToZonedDateTime(double decimalHours, LocalDate date, ZoneId zoneId) {
        long totalSeconds = (long) (decimalHours * 3600);
        ZonedDateTime startOfDayUTC = date.atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime preciseTimeUTC = startOfDayUTC.plusSeconds(totalSeconds);

        // Shifts the UTC time to the specific Local Zone requested by the user
        return preciseTimeUTC.withZoneSameInstant(zoneId);
    }
}