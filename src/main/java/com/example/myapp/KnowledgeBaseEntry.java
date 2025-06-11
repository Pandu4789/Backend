package com.example.myapp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeBaseEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob // Specifies that this should be stored as a Large Object (for long text)
    @Column(columnDefinition = "TEXT")
    private String content;

    // A One-to-One relationship: Each priest (User) has one KnowledgeBase entry.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priest_id", unique = true) // The foreign key in this table
    @JsonIgnore // Prevent infinite loops
    private User priest;
}