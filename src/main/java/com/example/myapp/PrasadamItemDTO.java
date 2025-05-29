package com.example.myapp;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrasadamItemDTO {
    private Long id;
    private String name;
    private String category;
    private Double price;
    private String description;
    private String imageUrl;
    private boolean available;
}
