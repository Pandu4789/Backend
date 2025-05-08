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
    private UserRepository userRepo;

    @GetMapping
    public ResponseEntity<?> getProfile(@RequestParam String username) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();
        Optional<Profile> profileOpt = profileRepo.findByUserUsername(username);

        Profile profile = profileOpt.orElseGet(() -> {
            Profile newProfile = new Profile();
            newProfile.setUser(user);
            // newProfile.setMailId(user.getemailID());
            return profileRepo.save(newProfile);
        });

        List<String> poojaNames = user.getPoojas().stream()
                .map(Event::getName)
                .toList();

        return ResponseEntity.ok(new ProfileResponse(
                profile.getId(),
                user.getUsername(),
                profile.getMailId(),
                profile.getProfilePicture(),
                profile.getBio(),
                user.getFirstName(),
                user.getLastName(),
                user.getAddress(),
                user.getPassword(),
                user.getPhone(),
                poojaNames
        ));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody Profile updatedProfile) {
        Optional<Profile> profileOpt = profileRepo.findById(updatedProfile.getId());
        if (profileOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Profile not found");
        }

        Profile profile = profileOpt.get();
        profile.setBio(updatedProfile.getBio());
        profile.setMailId(updatedProfile.getMailId());
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

        String filePath = "/uploads/" + file.getOriginalFilename(); // Replace with real logic
        Profile profile = profileOpt.get();
        profile.setProfilePicture(filePath);
        profileRepo.save(profile);

        return ResponseEntity.ok("Profile picture updated");
    }

    // Inner static class for response
    public static class ProfileResponse {
        private Long id;
        private String username;
        private String mailId;
        private String profilePicture;
        private String bio;
        private String firstName;
        private String lastName;
        private String address;
        private String password;
        private String phone;
        private List<String> services;

        public ProfileResponse(Long id, String username, String mailId, String profilePicture, String bio,
                               String firstName, String lastName, String address,
                               String password, String phone, List<String> services) {
            this.id = id;
            this.username = username;
            this.mailId = mailId;
            this.profilePicture = profilePicture;
            this.bio = bio;
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.password = password;
            this.phone = phone;
            this.services = services;
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getMailId() { return mailId; }
        public String getProfilePicture() { return profilePicture; }
        public String getBio() { return bio; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getAddress() { return address; }
        public String getPassword() { return password; }
        public String getPhone() { return phone; }
        public List<String> getServices() { return services; }
    }
}
