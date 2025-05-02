package com.example.myapp;
import java.util.*;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DailyTimesRepository extends JpaRepository<DailyTimes, Long> {
    Optional<DailyTimes> findByDate(LocalDate date);
}
