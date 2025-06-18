package com.example.myapp;

import java.time.LocalDate;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository extends JpaRepository<FestivalEntity, Long> {
        List<FestivalEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

}
