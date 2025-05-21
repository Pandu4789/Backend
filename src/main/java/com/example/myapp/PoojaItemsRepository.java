package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

// ✅ Repository for PoojaItems
@Repository
public interface PoojaItemsRepository extends JpaRepository<PoojaItems, Long> {
    List<PoojaItems> findByEvents_Id(Long eventId);
   Optional<PoojaItems> findById(Long id); // only if itemName is a field in the entity
}


