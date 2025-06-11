package com.example.myapp;

import lombok.Data;

@Data
public class AppointmentBookingResponseDto {
    private Long id;
    private Long eventId;
    private String name;
    private String phone;
    private String address;
    private String note;
    private String date;
    private String start;
    private String end;
    private String status;
    private Long priestId;
    private String priestName;
    private Long userId; // The user who made the appointment, if applicable

    public AppointmentBookingResponseDto(Long id, Long eventId, String name, String phone, String address,
                                         String note, String date, String start, String end,
                                         String status, Long priestId, String priestName, Long userId) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.note = note;
        this.date = date;
        this.start = start;
        this.end = end;
        this.status = status;
        this.priestId = priestId;
        this.priestName = priestName;
        this.userId = userId; // Assuming this is the user ID who made the appointment
    }
}
