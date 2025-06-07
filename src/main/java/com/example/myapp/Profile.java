package com.example.myapp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private User user;

    private String profilePicture;

    // Removed bio and mailId as they are now in Priest and User respectively.
    // If you still need a 'bio' for general users (not just priests), you can keep it here.
    // If 'mailId' is different from 'email', keep it here. Otherwise, use User.email.

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    // If you decide to keep general bio and mailId in Profile:
    // public String getBio() { return bio; }
    // public void setBio(String bio) { this.bio = bio; }
    // public String getMailId() { return mailId; }
    // public void setMailId(String mailId) { this.mailId = mailId; }
}