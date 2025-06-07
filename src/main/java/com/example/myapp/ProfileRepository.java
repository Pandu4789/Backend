package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // Assuming User entity's email is the primary way to find the linked profile
    Optional<Profile> findByUserEmail(String email); // New method
    // Optional<Profile> findByUserUsername(String username); // Old method, remove if not used
     Optional<Profile> findByUserId(Long userId);
}