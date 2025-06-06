package com.example.myapp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // Fetch lazily
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    @JsonIgnore // Ensure User does not try to serialize Profile back to User
    private User user;
   
    private String profilePicture; // Store URL or path to image
    private String bio; // This is now redundant if User/Priest also has bio. Consider removing.
    private String mailId; // This is now redundant with User.email. Consider removing.

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBio() { // Consider deprecating/removing if Priest entity has bio
        return bio;
    }

    public void setBio(String bio) { // Consider deprecating/removing if Priest entity has bio
        this.bio = bio;
    }

    public String getMailId() { // Consider deprecating/removing if User entity has email
        return mailId;
    }

    public void setMailId(String mailId) { // Consider deprecating/removing if User entity has email
        this.mailId = mailId;
    }
}