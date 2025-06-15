package com.example.myapp.panchang;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Panchangam {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String paksham;
    private String tithi;
    private String vara;
    private String nakshatram;
    private String muhurtamLagna;
    private String muhurtamTime;
    private String notes;
    // The old fields for rahukalam, etc., have been REMOVED from this entity.
    public String getMuhurthamLagnam() {
        throw new UnsupportedOperationException("Unimplemented method 'getMuhurthamLagnam'");
    }
}