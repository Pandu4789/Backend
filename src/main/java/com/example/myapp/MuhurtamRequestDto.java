package com.example.myapp;

import lombok.Data;

@Data
public class MuhurtamRequestDto {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String nakshatram;
    private String date;   // expected format YYYY-MM-DD
    private String time;   // expected format HH:mm or HH:mm:ss
    private String place;
    private String note;
    private Long priestId; // priest ID instead of full priest object
}
