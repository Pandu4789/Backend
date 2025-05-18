package com.example.myapp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String event;
    private String name;
    private String phone;
    private String address;
    private String note;
    private String date;   // Format: YYYY-MM-DD
    private String start;  // Format: HH:mm
    private String end;    // Format: HH:mm

    @Column(nullable = false)
    private String status; // "Pending", "Accepted", or "Rejected"

     @ManyToOne
    @JoinColumn(name = "priest_id", nullable = false)
    private User priest; 
}
