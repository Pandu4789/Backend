
package com.example.myapp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dashboard_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;
 @Column(nullable = true)
    private String photoUrl;
}
