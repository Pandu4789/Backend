package com.example.myapp.panchang;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Entity
@Table(name = "daily_times")
@Data // Using Lombok for getters/setters
public class DailyTimes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    // Inauspicious Times
    private LocalTime yamagandamStart, yamagandamEnd;
    private LocalTime rahukalamStart, rahukalamEnd;
    private LocalTime varjamStart, varjamEnd;
    private LocalTime durmohurtamStart, durmohurtamEnd;

    // ✅ ADD FIELDS FOR ALL 12 LAGNAS
    // Store Dallas-specific Lagna start and end times here
    private LocalTime meshaLagnaStart, meshaLagnaEnd;
    private LocalTime vrishabhaLagnaStart, vrishabhaLagnaEnd;
    private LocalTime mithunaLagnaStart, mithunaLagnaEnd;
    private LocalTime karkaLagnaStart, karkaLagnaEnd;
    private LocalTime simhaLagnaStart, simhaLagnaEnd;
    private LocalTime kanyaLagnaStart, kanyaLagnaEnd;
    private LocalTime tulaLagnaStart, tulaLagnaEnd;
    private LocalTime vrischikaLagnaStart, vrischikaLagnaEnd;
    private LocalTime dhanuLagnaStart, dhanuLagnaEnd;
    private LocalTime makaraLagnaStart, makaraLagnaEnd;
    private LocalTime kumbhaLagnaStart, kumbhaLagnaEnd;
    private LocalTime meenaLagnaStart, meenaLagnaEnd;
}