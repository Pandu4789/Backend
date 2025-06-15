package com.example.myapp.panchang;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MuhurtamFindResponseDto {
    private LocalDate date;
    private String tithi;
    private String nakshatram;
    private String status; // green, red, orange, gray
    private String muhurtamLagna;
    private String muhurtamTime;
    private String alternateTime; // optional, for orange status
    private String notes;
}