package com.example.myapp;

import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentBookingDto {
    private Long eventId;
    private String name;
    private String phone;
    private String address;
    private String note;
    private String date;
    private String start;
    private String end;
    private Long priestId;
    private Long userId; 
}

