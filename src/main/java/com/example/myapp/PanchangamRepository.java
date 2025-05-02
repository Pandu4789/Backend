package com.example.myapp;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PanchangamRepository extends JpaRepository<Panchangam, Long> {
    List<Panchangam> findByNakshatram(String nakshatram);
}
