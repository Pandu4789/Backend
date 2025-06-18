package com.example.myapp; // Or your relevant package

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class GalleryImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String caption;
    private Long priestId;
    private String priestName;
    private LocalDate uploadDate;
}