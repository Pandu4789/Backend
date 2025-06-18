package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {
    List<GalleryImage> findByPriestId(Long priestId);

    // Custom query to count uploads for a priest in a specific month
    long countByPriestIdAndUploadDateBetween(Long priestId, LocalDate startOfMonth, LocalDate endOfMonth);
}