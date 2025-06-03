package com.example.myapp;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "panchangam")
public class Panchangam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String paksha;
    private String tithi;
    private String vaaram;
    private String nakshatram;
    private String lagnam;
    private String mohurtam;
    private String time;
    private String notes;

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
    public String getPaksha() {
        return paksha;
    }
    public void setPaksha(String paksha) {
        this.paksha = paksha;
    }
    public String getTithi() {
        return tithi;
    }
    public void setTithi(String tithi) {
        this.tithi = tithi;
    }
    public String getVaaram() {
        return vaaram;
    }
    public void setVaaram(String vaaram) {
        this.vaaram = vaaram;
    }
    public String getNakshatram() {
        return nakshatram;
    }
    public void setNakshatram(String nakshatram) {
        this.nakshatram = nakshatram;
    }
    public String getLagnam() {
        return lagnam;
    }
    public void setLagnam(String lagnam) {
        this.lagnam = lagnam;
    }
    public String getMohurtam() {
        return mohurtam;
    }
    public void setMohurtam(String mohurtam) {
        this.mohurtam = mohurtam;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

}
