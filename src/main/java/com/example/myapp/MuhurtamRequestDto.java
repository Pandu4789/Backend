package com.example.myapp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MuhurtamRequestDto {
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
    private Long priestId;
    private String priestEmail;

    public MuhurtamRequestDto(MuhurtamRequest request) {
        this.id = request.getId();
        this.name = request.getName();
        this.email = request.getEmail();
        this.phone = request.getPhone();
        this.address = request.getAddress();
        this.nakshatram = request.getNakshatram();
        this.date = request.getDate();
        this.time = request.getTime();
        this.place = request.getPlace();
        this.note = request.getNote();
        this.viewed = request.isViewed();
        this.priestId = request.getPriest().getId();
        this.priestEmail = request.getPriest().getEmail();
    }
}
