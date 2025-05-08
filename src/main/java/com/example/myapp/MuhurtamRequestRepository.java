package com.example.myapp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MuhurtamRequestRepository extends JpaRepository<MuhurtamRequest, Long> {
}

