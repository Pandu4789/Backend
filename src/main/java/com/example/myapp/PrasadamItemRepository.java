package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrasadamItemRepository extends JpaRepository<PrasadamItem, Long> {
    List<PrasadamItem> findByCategory(String category);
    List<PrasadamItem> findByAvailableTrue();
}
