package com.example.myapp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBaseEntry, Long> {
    Optional<KnowledgeBaseEntry> findByPriestId(Long priestId);
}
