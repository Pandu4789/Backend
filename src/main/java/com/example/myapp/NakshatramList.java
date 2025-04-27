package com.example.myapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class NakshatramList {

    @Bean
    CommandLineRunner loadNakshatrams(NakshatramRepository repository) {
        return args -> {
            List<String> nakshatrams = List.of(
                "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra",
                "Punarvasu", "Pushya", "Ashlesha", "Magha", "Purva Phalguni",
                "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha",
                "Anuradha", "Jyeshtha", "Mula", "Purva Ashadha", "Uttara Ashadha",
                "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada",
                "Uttara Bhadrapada", "Revati"
            );

            nakshatrams.forEach(name -> {
                if (!repository.existsByName(name)) {
                    Nakshatram nakshatram = new Nakshatram();
                    nakshatram.setName(name);
                    repository.save(nakshatram);
                }
            });
        };
    }
}
