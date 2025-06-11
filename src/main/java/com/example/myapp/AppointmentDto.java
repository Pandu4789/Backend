// Create this new file: AppointmentDto.java
package com.example.myapp;

import lombok.Data;

@Data
public class AppointmentDto {
    private String name;
    private String phone;
    private String address;
    private String events;
    private String note;
    private String date;  
    private String start;  
    private String end;    
    private Long priestId;
}