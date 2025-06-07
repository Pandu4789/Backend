package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Ensure this matches your frontend URL
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PriestRepository priestRepo; // Autowire PriestRepository

    // --- DTOs ---

    // DTO for fetching profile data
    public static class ProfileResponse {
        private Long profileId;
        private Long userId;
        private String email; // User's email, used as mailId on frontend
        private String profilePicture; // From Profile entity
        private String bio; // From Priest entity (if applicable)
        private String firstName; // From User entity
        private String lastName;  // From User entity
        private String phone;     // From User entity
        private String addressLine1; // From User entity
        private String addressLine2; // From User entity
        private String city;         // From User entity
        private String state;        // From User entity
        private String zipCode;      // From User entity
        private List<String> services; // From Priest entity (if applicable)
        private List<String> languages; // From Priest entity (if applicable)
        private String role; // User's role

        // Constructor
        public ProfileResponse(Long profileId, Long userId, String email, String profilePicture, String bio,
                               String firstName, String lastName, String phone,
                               String addressLine1, String addressLine2, String city, String state, String zipCode,
                               List<String> services, List<String> languages, String role) {
            this.profileId = profileId;
            this.userId = userId;
            this.email = email;
            this.profilePicture = profilePicture;
            this.bio = bio;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.addressLine1 = addressLine1;
            this.addressLine2 = addressLine2;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.services = services;
            this.languages = languages;
            this.role = role;
        }

        // Getters (needed for JSON serialization)
        public Long getProfileId() { return profileId; }
        public Long getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getProfilePicture() { return profilePicture; }
        public String getBio() { return bio; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPhone() { return phone; }
        public String getAddressLine1() { return addressLine1; }
        public String getAddressLine2() { return addressLine2; }
        public String getCity() { return city; }
        public String getState() { return state; }
        public String getZipCode() { return zipCode; }
        public List<String> getServices() { return services; }
        public List<String> getLanguages() { return languages; }
        public String getRole() { return role; }

        // Setters are not strictly needed for a response DTO if only used for constructing JSON
    }

    // DTO for updating profile data (from frontend to backend)
    public static class ProfileUpdateRequest {
        private Long userId; // Required to identify the user
        private Long profileId; // Required to identify the profile entity for picture/mailId update
        private String firstName;
        private String lastName;
        private String phone;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String zipCode;
        private String mailId; // Represents the user's email for update
        private String bio; // For priest
        private List<String> services; // For priest
        private List<String> languages; // For priest

        // Getters and Setters (needed for JSON deserialization)
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getProfileId() { return profileId; }
        public void setProfileId(Long profileId) { this.profileId = profileId; }
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
        public String getMailId() { return mailId; }
        public void setMailId(String mailId) { this.mailId = mailId; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public List<String> getServices() { return services; }
        public void setServices(List<String> services) { this.services = services; }
        public List<String> getLanguages() { return languages; }
        public void setLanguages(List<String> languages) { this.languages = languages; }
    }


    // --- Endpoints ---

    @GetMapping
    public ResponseEntity<?> getProfile(@RequestParam String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        User user = userOpt.get();

        // Ensure Profile exists for the user, create if not
        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
            profile = profileRepo.save(profile);
            user.setProfile(profile); // Link the new profile back to the user
            userRepo.save(user); // Save user to persist the profile link
        }

        String bio = null;
        List<String> services = new ArrayList<>();
        List<String> languages = new ArrayList<>();
        if ("priest".equalsIgnoreCase(user.getRole())) {
            Priest priestDetails = user.getPriestDetails();
            if (priestDetails != null) {
                bio = priestDetails.getBio();
                services = priestDetails.getServicesOffered();
                languages = priestDetails.getLanguagesSpoken();
            }
        }

        // Frontend expects 'username' but your User model uses 'email'.
        // The frontend also expects 'mailId'. Mapping User.email to ProfileResponse.email.
        return ResponseEntity.ok(new ProfileResponse(
                profile.getId(),          // profileId
                user.getId(),             // userId
                user.getEmail(),          // email (becomes mailId on frontend)
                profile.getProfilePicture(),
                bio,                      // Priest's bio
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getAddressLine1(),
                user.getAddressLine2(),
                user.getCity(),
                user.getState(),
                user.getZipCode(),
                services,                 // Priest's services
                languages,                // Priest's languages
                user.getRole()
        ));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest request) {
        Optional<User> userOpt = userRepo.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }
        User user = userOpt.get();

        // 1. Update User details
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddressLine1(request.getAddressLine1());
        user.setAddressLine2(request.getAddressLine2());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setZipCode(request.getZipCode());
        // Do NOT update password here. Handle it via a separate endpoint if needed.
        user.setEmail(request.getMailId()); // Update user's email if mailId is passed

        userRepo.save(user); // Save updated user

        // 2. Update Profile details (for profile picture path, mailId if it were there, etc.)
        Optional<Profile> profileOpt = profileRepo.findById(request.getProfileId());
        if (profileOpt.isEmpty()) {
            // This should ideally not happen if a profile is always created with a user
            return ResponseEntity.status(404).body("Profile record not found for user.");
        }
        Profile profile = profileOpt.get();
        // profile.setMailId(request.getMailId()); // If Profile had a mailId field, update it here.
                                                 // Currently, email is on User.
        profileRepo.save(profile);

        // 3. Update Priest details if applicable
        if ("priest".equalsIgnoreCase(user.getRole())) {
            Priest priestDetails = user.getPriestDetails();
            if (priestDetails == null) {
                // If priestDetails is null but role is priest, create a new Priest record
                priestDetails = new Priest();
                priestDetails.setUser(user);
                user.setPriestDetails(priestDetails); // Link back to user
            }
            priestDetails.setBio(request.getBio());
            priestDetails.setServicesOffered(request.getServices());
            priestDetails.setLanguagesSpoken(request.getLanguages());
            priestRepo.save(priestDetails); // Save updated priest details
        }

        return ResponseEntity.ok("Profile updated successfully!");
    }

    @PostMapping("/updateProfilePicture")
    public ResponseEntity<?> updateProfilePicture(@RequestParam Long profileId,
                                                  @RequestParam("profilePicture") MultipartFile file) {
        Optional<Profile> profileOpt = profileRepo.findById(profileId);
        if (profileOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Profile not found.");
        }

        // In a real application, you'd store the file securely and get a unique path.
        // For local development, ensure your Spring Boot app can serve static content
        // from the /uploads directory.
        String filePath = "/uploads/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        // Save the file to the file system (outside of this controller, e.g., in a service)
        // Example: Files.copy(file.getInputStream(), Paths.get("path/to/your/uploads", file.getOriginalFilename()));

        Profile profile = profileOpt.get();
        profile.setProfilePicture(filePath);
        profileRepo.save(profile);

        return ResponseEntity.ok(Map.of("message", "Profile picture updated successfully!", "newPath", filePath));
    }
}