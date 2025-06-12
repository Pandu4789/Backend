package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentBookingRepository extends JpaRepository<AppointmentBooking, Long> {
    List<AppointmentBooking> findByStatus(String status);
    List<AppointmentBooking> findByPriestId(Long priestId);
    List<AppointmentBooking> findByUserId(Long userId);
    List<AppointmentBooking> findByPriestIdAndDateBetween(Long priestId, String start, String end);

    
}
