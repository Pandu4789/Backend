package com.example.myapp;

import lombok.Data;
import java.util.List;

@Data
public class PriestAvailabilityDto {
    private Long priestId;
    private String date; // "yyyy-MM-dd"
    private List<String> unavailableSlots; // List of times like "09:00", "10:00"
}