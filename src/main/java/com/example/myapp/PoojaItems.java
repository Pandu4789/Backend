package com.example.myapp;

import java.util.HashSet;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;


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

    @ManyToMany
    @JoinTable(
        name = "event_pooja_items",
        joinColumns = @JoinColumn(name = "pooja_item_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id")
    )
   


    private Set<Event> events = new HashSet<>();


   
   

    // Getters and Setters
}
