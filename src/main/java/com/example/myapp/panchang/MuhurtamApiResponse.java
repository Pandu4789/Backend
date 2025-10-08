package com.example.myapp.panchang;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Using Lombok for boilerplate code
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MuhurtamApiResponse {
    private List<MuhurtamFindResponseDto> dailyResults;
    private Set<String> favorableNakshatrams;
}