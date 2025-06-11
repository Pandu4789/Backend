package com.example.myapp;

import lombok.Data;

@Data
public class EventDto {
    private String name;
    private String description;
    private String category;
    private String duration;
    private String estimatedPrice;
}