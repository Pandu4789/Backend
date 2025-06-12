package com.example.myapp;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriestAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priest_id", nullable = false)
    private User priest;

    @Column(nullable = false)
    private LocalDate slotDate;

    @Column(nullable = false)
    private LocalTime slotTime;
    
    // We only need to store "Unavailable" slots. 
    // If a record exists, the slot is unavailable. If it doesn't, it's available.
}