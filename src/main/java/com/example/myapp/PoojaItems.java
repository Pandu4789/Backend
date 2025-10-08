package com.example.myapp;

import com.fasterxml.jackson.annotation.JsonIgnore; // ✅ 1. ADD THIS IMPORT
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pooja_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoojaItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private int quantity;
    private double unitPrice;
    private String unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore // ✅ 2. ADD THIS ANNOTATION
    private Event event;
}

