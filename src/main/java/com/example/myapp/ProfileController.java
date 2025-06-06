package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepo;

    @Autowired
    private UserRepository userRepo; // Still need UserRepository to find user

    // Response DTO for Profile
    public static class ProfileResponse {
        private Long profileId;
        private Long userId;
        private String email; // Changed from username
        private String profilePicture;
        private String bio; // From Profile entity
        private String firstName;
        private String lastName;
        private String addressLine1; // New
        private String addressLine2; // New
        private String city;         // New
        private String state;        // New
        private String zipCode;      // New
        private String phone;
        private List<String> services; // From Priest entity, if user is a priest

        public ProfileResponse(Long profileId, Long userId, String email, String profilePicture, String bio,
                               String firstName, String lastName, String addressLine1, String addressLine2,
                               String city, String state, String zipCode, String phone, List<String> services) {
            this.profileId = profileId;
            this.userId = userId;
            this.email = email;
            this.profilePicture = profilePicture;
            this.bio = bio;
            this.firstName = firstName;
            this.lastName = lastName;
            this.addressLine1 = addressLine1;
            this.addressLine2 = addressLine2;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.phone = phone;
            this.services = services;
        }

        // Getters
        public Long getProfileId() { return profileId; }
        public Long getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getProfilePicture() { return profilePicture; }
        public String getBio() { return bio; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getAddressLine1() { return addressLine1; }
        public String getAddressLine2() { return addressLine2; }
        public String getCity() { return city; }
        public String getState() { return state; }
        public String getZipCode() { return zipCode; }
        public String getPhone() { return phone; }
        public List<String> getServices() { return services; }
    }


    @GetMapping
    public ResponseEntity<?> getProfile(@RequestParam String email) { // Changed to use email
        Optional<User> userOpt = userRepo.findByEmail(email); // Find by email
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        // Find or create profile if not exists
        Profile profile = profileRepo.findByUserEmail(email) // You'll need to add findByUserEmail to ProfileRepository
                .orElseGet(() -> {
                    Profile newProfile = new Profile();
                    newProfile.setUser(user);
                    return profileRepo.save(newProfile);
                });

        // Get priest services if user is a priest
        List<String> services = new ArrayList<>();
        if ("priest".equalsIgnoreCase(user.getRole()) && user.getPriestDetails() != null) {
            services = user.getPriestDetails().getServicesOffered();
        }

        // Return full profile response
        return ResponseEntity.ok(new ProfileResponse(
                profile.getId(),
                user.getId(),
                user.getEmail(), // Use user's email
                profile.getProfilePicture(),
                profile.getBio(), // Using Profile's bio, but Priest has one too. Decide which to use.
                user.getFirstName(),
                user.getLastName(),
                user.getAddressLine1(),
                user.getAddressLine2(),
                user.getCity(),
                user.getState(),
                user.getZipCode(),
                user.getPhone(),
                services
        ));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody Profile updatedProfile) {
        // This method needs to be aligned with the new User/Priest structure.
        // It's currently only updating Profile entity fields.
        // If bio and mailId are now on User/Priest, this needs a DTO that combines them
        // and logic to update User/Priest entities as well.
        // For now, minimal change to compile:
        Optional<Profile> profileOpt = profileRepo.findById(updatedProfile.getId());
        if (profileOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Profile not found");
        }

        Profile profile = profileOpt.get();
        // profile.setBio(updatedProfile.getBio()); // Bio is now on Priest
        // profile.setMailId(updatedProfile.getMailId()); // MailId is now on User (email)
        profile.setProfilePicture(updatedProfile.getProfilePicture());

        profileRepo.save(profile);
        return ResponseEntity.ok("Profile updated");
    }

    @PostMapping("/updateProfilePicture")
    public ResponseEntity<?> updateProfilePicture(@RequestParam Long profileId,
                                                  @RequestParam("profilePicture") MultipartFile file) {
        Optional<Profile> profileOpt = profileRepo.findById(profileId);
        if (profileOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Profile not found");
        }

        String filePath = "/uploads/" + file.getOriginalFilename();

        Profile profile = profileOpt.get();
        profile.setProfilePicture(filePath);
        profileRepo.save(profile);

        return ResponseEntity.ok("Profile picture updated");
    }
}