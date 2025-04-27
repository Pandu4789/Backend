package com.example.myapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class EventList {

    @Bean
    CommandLineRunner loadEvents(EventRepository repository) {
        return args -> {
            List<String> events = List.of(
                "Archana", "Archana (Sahasranama)", "Abhishekam (Individual)", "Abhishekam (Group)", 
                "Abhishekam (Temple scheduled)", "Bhagavthi Seva", "Havan (Private Sponsored)", "Havan (Temple scheduled)", 
                "Mundan – Hair Offering", "Kalyana Utsavam (in Temple)", "Namkaran Ceremony",
                "Satyanarayana Puja (Private in Temple)", "Satyanarayana Puja", "Shraaddha-Havan",
                "Shraaddha Tarapan/Hiranya", "Upanayana Thread Ceremony", "Upanayana (at Home)", "Vahana Puja",
                "Wedding (in Temple)", "Wedding (Outside)", "Sunday Maha Prasad Sponsor", "Funeral"
            );

            events.forEach(name -> {
                if (!repository.existsByName(name)) {
                    repository.save(new Event(name));
                }
            });
        };
    }
}
