package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PoojaItemsRepository extends JpaRepository<PoojaItems, Long> {
    
    // ✅ CHANGED: The method now queries by the 'event' field's 'id' property.
    List<PoojaItems> findByEvent_Id(Long eventId);

    // This method is redundant as JpaRepository already provides it. Can be removed.
    // Optional<PoojaItems> findById(Long id); 
}
