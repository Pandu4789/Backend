package com.example.myapp;

import java.util.List;

public class NakshatramResponseDTO {
    private String nakshatram;
    private List<ScheduleDTO> schedules;

    // Constructor
    public NakshatramResponseDTO(String nakshatram, List<ScheduleDTO> schedules) {
        this.nakshatram = nakshatram;
        this.schedules = schedules;
    }

    // Getters and Setters
    public String getNakshatram() { return nakshatram; }
    public void setNakshatram(String nakshatram) { this.nakshatram = nakshatram; }
    public List<ScheduleDTO> getSchedules() { return schedules; }
    public void setSchedules(List<ScheduleDTO> schedules) { this.schedules = schedules; }
}
