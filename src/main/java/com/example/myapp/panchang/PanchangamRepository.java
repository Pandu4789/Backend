package com.example.myapp.panchang;

import java.time.LocalDate;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PanchangamRepository extends JpaRepository<Panchangam, LocalDate> {
        List<Panchangam> findByNakshatramInAndDateAfter(List<String> commonFavorableNakshatrams, LocalDate now);
        List<Panchangam> findByNakshatram(String name);
List<Panchangam> findByDate(LocalDate date);
    
}
