package com.example.myapp;


import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardEventRepository extends JpaRepository<DashboardEvent, Long> {
}
