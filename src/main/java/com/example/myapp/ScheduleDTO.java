package com.example.myapp;

import java.time.LocalDate;

public class ScheduleDTO {
    private LocalDate date;
    private String paksha;
    private String tithi;
    private String vaaram;
    private String lagnam;
    private String Mohurtam;
    private String Time;
    private String notes;
    private String yamagandam;
    private String rahukalam;
    private String varjam;
    private String durmohurtam;

    // Constructors
    public ScheduleDTO() {}

    // Getters and Setters
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
    public String getMohurtam() { return Mohurtam; }
    public void setTimeMohurtam(String Mohurtam) { this.Mohurtam = Mohurtam; }
    public String getTime() { return Time; }
    public void setTime(String Time) { this.Time = Time; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getYamagandam() { return yamagandam; }
    public void setYamagandam(String yamagandam) { this.yamagandam = yamagandam; }
    public String getRahukalam() { return rahukalam; }
    public void setRahukalam(String rahukalam) { this.rahukalam = rahukalam; }
    public String getVarjam() { return varjam; }
    public void setVarjam(String varjam) { this.varjam = varjam; }
    public String getDurmohurtam() { return durmohurtam; }
    public void setDurmohurtam(String durmohurtam) { this.durmohurtam = durmohurtam; }

    public void setMohurtam(String mohurtam2) {
        throw new UnsupportedOperationException("Unimplemented method 'setMohurtam'");
    }
}
