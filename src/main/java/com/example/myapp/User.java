package com.example.myapp;

import jakarta.persistence.*;

@Entity
@Table(name = "User") // Renamed table to avoid conflicts with SQL keywords like 'user'
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Email should be unique for login
    private String email; // New field for email login

    @Column(nullable = false)
    private String password;

    private String role; // customer, priest, admin

    private String firstName;
    private String lastName;
    private String phone;
    private String addressLine1; // New field
    private String addressLine2; // New field
    private String city;         // New field
    private String state;        // New field
    private String zipCode;      // New field

    // Removed: private String username; (if email is now the primary identifier)
    // Removed: private String address; (split into addressLine1, addressLine2, city, state, zipCode)
    // Removed: @ManyToMany List<Event> poojas; (moved to Priest entity)

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Priest priestDetails; // New OneToOne relationship to Priest

    // If you still need Profile for picture etc., ensure mappedBy matches correctly
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;


    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; } // Corrected getter name
    public void setRole(String role) { this.role = role; } // Corrected setter name

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public Priest getPriestDetails() { return priestDetails; }
    public void setPriestDetails(Priest priestDetails) {
        if (priestDetails != null) {
            priestDetails.setUser(this);
        }
        this.priestDetails = priestDetails;
    }

    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) {
        if (profile != null) {
            profile.setUser(this);
        }
        this.profile = profile;
    }
}