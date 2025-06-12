package com.example.myapp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
     List<Appointment> findByPriestId(Long priestId);
List<Appointment> findByPriestIdAndStartBetween(Long priestId, LocalDateTime startDate, LocalDateTime endDate);
}

