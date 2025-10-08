package com.example.myapp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardEventRepository extends JpaRepository<DashboardEvent, Long> {
        List<DashboardEvent> findByPriestId(Long priestId);

}
