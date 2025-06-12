package com.example.myapp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;
    
    private String date; // Keep as String "yyyy-MM-dd"

    // ✅ ADD THESE NEW FIELDS
    private String eventTime; // To store time like "18:00"
    private String imageUrl;  // To store the URL of the uploaded image
}