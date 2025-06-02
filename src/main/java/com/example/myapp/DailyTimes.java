package com.example.myapp;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "daily_times")
public class DailyTimes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private LocalTime yamagandamStart;
    private LocalTime yamagandamEnd;

    private LocalTime rahukalamStart;
    private LocalTime rahukalamEnd;

    private LocalTime varjamStart;
    private LocalTime varjamEnd;

    private LocalTime durmohurtamStart;
    private LocalTime durmohurtamEnd;

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getYamagandamStart() { return yamagandamStart; }
    public void setYamagandamStart(LocalTime yamagandamStart) { this.yamagandamStart = yamagandamStart; }

    public LocalTime getYamagandamEnd() { return yamagandamEnd; }
    public void setYamagandamEnd(LocalTime yamagandamEnd) { this.yamagandamEnd = yamagandamEnd; }

    public LocalTime getRahukalamStart() { return rahukalamStart; }
    public void setRahukalamStart(LocalTime rahukalamStart) { this.rahukalamStart = rahukalamStart; }

    public LocalTime getRahukalamEnd() { return rahukalamEnd; }
    public void setRahukalamEnd(LocalTime rahukalamEnd) { this.rahukalamEnd = rahukalamEnd; }

    public LocalTime getVarjamStart() { return varjamStart; }
    public void setVarjamStart(LocalTime varjamStart) { this.varjamStart = varjamStart; }

    public LocalTime getVarjamEnd() { return varjamEnd; }
    public void setVarjamEnd(LocalTime varjamEnd) { this.varjamEnd = varjamEnd; }

    public LocalTime getDurmohurtamStart() { return durmohurtamStart; }
    public void setDurmohurtamStart(LocalTime durmohurtamStart) { this.durmohurtamStart = durmohurtamStart; }

    public LocalTime getDurmohurtamEnd() { return durmohurtamEnd; }
    public void setDurmohurtamEnd(LocalTime durmohurtamEnd) { this.durmohurtamEnd = durmohurtamEnd; }
}
