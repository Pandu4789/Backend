package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PriestRepository extends JpaRepository<Priest, Long> {
    Optional<Priest> findByUserEmail(String email); // Find priest by associated user's email
}