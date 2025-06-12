package com.example.myapp;

import lombok.Data;
import java.time.format.DateTimeFormatter;

@Data
public class PriestAvailabilityResponseDto {
    private String slotDate;
    private String slotTime;

    // A constructor to easily map from the entity to this DTO
    public PriestAvailabilityResponseDto(PriestAvailability entity) {
        this.slotDate = entity.getSlotDate().toString(); // Converts LocalDate to "yyyy-MM-dd"
        this.slotTime = entity.getSlotTime().format(DateTimeFormatter.ofPattern("HH:mm")); // Converts LocalTime to "HH:mm"
    }
}