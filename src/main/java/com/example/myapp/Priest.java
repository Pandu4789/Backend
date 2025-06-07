package com.example.myapp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Priest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference 
    private User user;

    @Column(length = 1000) 
    private String bio;

    @ElementCollection 
    @CollectionTable(name = "priest_services", joinColumns = @JoinColumn(name = "priest_id"))
    @Column(name = "service_name") 
    private List<String> servicesOffered; 

    @ElementCollection
     @CollectionTable(name = "priest_languages", joinColumns = @JoinColumn(name = "priest_id")) 
    @Column(name = "language_name") 
    private List<String> languagesSpoken;

    private boolean offersHoroscopeReading;

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public List<String> getServicesOffered() { return servicesOffered; }
    public void setServicesOffered(List<String> servicesOffered) { this.servicesOffered = servicesOffered; }

    public List<String> getLanguagesSpoken() { return languagesSpoken; }
    public void setLanguagesSpoken(List<String> languagesSpoken) { this.languagesSpoken = languagesSpoken; }

    public boolean isOffersHoroscopeReading() { return offersHoroscopeReading; }
    public void setOffersHoroscopeReading(boolean offersHoroscopeReading) { this.offersHoroscopeReading = offersHoroscopeReading; }
}