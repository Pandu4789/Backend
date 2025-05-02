package com.example.myapp;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_times")
public class DailyTimes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String yamagandam;
    private String rahukalam;
    private String varjam;
    private String durmohurtam;

    // Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getYamagandam() {
        return yamagandam;
    }
    public void setYamagandam(String yamagandam) {
        this.yamagandam = yamagandam;
    }
    public String getRahukalam() {
        return rahukalam;
    }
    public void setRahukalam(String rahukalam) {
        this.rahukalam = rahukalam;
    }       
    public String getVarjam() {
        return varjam;
    }
    public void setVarjam(String varjam) {
        this.varjam = varjam;
    }
    public String getDurmohurtam() {
        return durmohurtam;
    }
    public void setDurmohurtam(String durmohurtam) {
        this.durmohurtam = durmohurtam;
    }
}
