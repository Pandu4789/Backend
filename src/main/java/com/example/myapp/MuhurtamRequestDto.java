package com.example.myapp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MuhurtamRequestDto {
    private Long id;
    private Long event;
    private String eventName;          // <-- new field
    private String name;
    private String email;
    private String phone;
    private String nakshatram;
    private String date;
    private String time;
    private String place;
    private String note;
    private boolean viewed;
    private Long priestId;
    private String priestEmail;
    private String priestName;
    private Long userId;

    public MuhurtamRequestDto(MuhurtamRequest request) {
        this.id = request.getId();
        if (request.getEvent() != null) {
            this.event = request.getEvent().getId();
            this.eventName = request.getEvent().getName();  // <-- set eventName here
        } else {
            this.event = null;
            this.eventName = null;
        }
        this.name = request.getName();
        this.email = request.getEmail();
        this.phone = request.getPhone();
        this.nakshatram = request.getNakshatram();
        this.date = request.getDate();
        this.time = request.getTime();
        this.place = request.getPlace();
        this.note = request.getNote();
        this.viewed = request.isViewed();
        this.priestId = request.getPriest().getId();
        this.priestEmail = request.getPriest().getEmail();
        this.priestName = request.getPriest().getFirstName() + " " + request.getPriest().getLastName();
        this.userId = request.getUser() != null ? request.getUser().getId() : null;
    }
}
