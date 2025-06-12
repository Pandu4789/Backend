package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PriestAvailabilityRepository extends JpaRepository<PriestAvailability, Long> {
    
    // Find all unavailable slots for a given priest
    List<PriestAvailability> findByPriestId(Long priestId);

    // Delete all entries for a specific priest on a specific date
    void deleteByPriestIdAndSlotDate(Long priestId, LocalDate slotDate);
}