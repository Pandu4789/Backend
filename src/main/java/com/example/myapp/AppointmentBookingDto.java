package com.example.myapp;

import lombok.Data;

@Data
public class AppointmentBookingDto {
    private String event;
    private String name;
    private String phone;
    private String address;
    private String note;
    private String date;
    private String start;
    private String end;
    private Long priestId;  // matches your JSON payload field
}
