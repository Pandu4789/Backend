package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NakshatramRepository extends JpaRepository<Nakshatram, Long> {
    boolean existsByName(String name);
}

