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

    // Many appointments can belong to one Event
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String name;
    private String phone;
    private String address;
    private String note;

    // Dates and times should ideally be stored as LocalDate and LocalTime
    // but kept as String here as per your existing format.
    private String date;   // Format: YYYY-MM-DD
    private String start;  // Format: HH:mm
    private String end;    // Format: HH:mm

    @Column(nullable = false)
    private String status; // Example values: "PENDING", "ACCEPTED", "REJECTED"

    // Many appointments can be associated with one priest
    @ManyToOne
    @JoinColumn(name = "priest_id")
    private User priest;
}
