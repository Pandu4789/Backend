package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MuhurtamRequestRepository extends JpaRepository<MuhurtamRequest, Long> {
    List<MuhurtamRequest> findByPriestId(Long priestId);
    List<MuhurtamRequest> findByUserId(Long userId);
}
