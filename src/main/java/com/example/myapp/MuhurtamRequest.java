package com.example.myapp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "muhurtam_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MuhurtamRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phone;

    private String address;

    private String nakshatram; 
    
    private String date;

    private String time;

    private String place;

    private String note;
    
    private boolean viewed;

     @ManyToOne
     @JsonIgnore
    @JoinColumn(name = "priest_id", nullable = false)
    private User priest;
}

