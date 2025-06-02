package com.example.myapp;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleDTO {
    private LocalDate date;
    private String paksha;
    private String tithi;
    private String vaaram;
    private String lagnam;
    private String mohurtam;
    private String time;
    private String notes;

    // Time range strings
    private String rahukalam;
    private String yamagandam;
    private String varjam;
    private String durmohurtam;

    // Optional raw time fields (for internal use)
    private LocalTime rahukalamStart;
    private LocalTime rahukalamEnd;
    private LocalTime yamagandamStart;
    private LocalTime yamagandamEnd;
    private LocalTime varjamStart;
    private LocalTime varjamEnd;
    private LocalTime durmohurtamStart;
    private LocalTime durmohurtamEnd;

    public ScheduleDTO() {}

    // Getters and setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getPaksha() { return paksha; }
    public void setPaksha(String paksha) { this.paksha = paksha; }

    public String getTithi() { return tithi; }
    public void setTithi(String tithi) { this.tithi = tithi; }

    public String getVaaram() { return vaaram; }
    public void setVaaram(String vaaram) { this.vaaram = vaaram; }

    public String getLagnam() { return lagnam; }
    public void setLagnam(String lagnam) { this.lagnam = lagnam; }

    public String getMohurtam() { return mohurtam; }
    public void setMohurtam(String mohurtam) { this.mohurtam = mohurtam; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getRahukalam() { return rahukalam; }
    public void setRahukalam(String rahukalam) { this.rahukalam = rahukalam; }

    public String getYamagandam() { return yamagandam; }
    public void setYamagandam(String yamagandam) { this.yamagandam = yamagandam; }

    public String getVarjam() { return varjam; }
    public void setVarjam(String varjam) { this.varjam = varjam; }

    public String getDurmohurtam() { return durmohurtam; }
    public void setDurmohurtam(String durmohurtam) { this.durmohurtam = durmohurtam; }

    // Optional raw time getters/setters
    public LocalTime getRahukalamStart() { return rahukalamStart; }
    public void setRahukalamStart(LocalTime rahukalamStart) { this.rahukalamStart = rahukalamStart; }

    public LocalTime getRahukalamEnd() { return rahukalamEnd; }
    public void setRahukalamEnd(LocalTime rahukalamEnd) { this.rahukalamEnd = rahukalamEnd; }

    public LocalTime getYamagandamStart() { return yamagandamStart; }
    public void setYamagandamStart(LocalTime yamagandamStart) { this.yamagandamStart = yamagandamStart; }

    public LocalTime getYamagandamEnd() { return yamagandamEnd; }
    public void setYamagandamEnd(LocalTime yamagandamEnd) { this.yamagandamEnd = yamagandamEnd; }

    public LocalTime getVarjamStart() { return varjamStart; }
    public void setVarjamStart(LocalTime varjamStart) { this.varjamStart = varjamStart; }

    public LocalTime getVarjamEnd() { return varjamEnd; }
    public void setVarjamEnd(LocalTime varjamEnd) { this.varjamEnd = varjamEnd; }

    public LocalTime getDurmohurtamStart() { return durmohurtamStart; }
    public void setDurmohurtamStart(LocalTime durmohurtamStart) { this.durmohurtamStart = durmohurtamStart; }

    public LocalTime getDurmohurtamEnd() { return durmohurtamEnd; }
    public void setDurmohurtamEnd(LocalTime durmohurtamEnd) { this.durmohurtamEnd = durmohurtamEnd; }
}
