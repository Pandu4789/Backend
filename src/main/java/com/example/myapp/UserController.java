package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PriestRepository priestRepo; // Autowire the new PriestRepository

    // Assuming EventRepository is still needed for fetching valid services if frontend sends IDs
    // @Autowired
    // private EventRepository eventRepo;

    // DTO for Signup Request (to handle mixed User/Priest data from frontend)
    public static class SignupRequest {
        private String email;
        private String password;
        private String role;
        private String firstName;
        private String lastName;
        private String phone;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String zipCode;
        // Priest specific fields
        private String bio;
        private List<String> servicesOffered; // Assuming frontend sends names/strings directly now
        private Boolean offersHoroscopeReading;

        // Getters and Setters for SignupRequest
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
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
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public List<String> getServicesOffered() { return servicesOffered; }
        public void setServicesOffered(List<String> servicesOffered) { this.servicesOffered = servicesOffered; }
        public Boolean getOffersHoroscopeReading() { return offersHoroscopeReading; }
        public void setOffersHoroscopeReading(Boolean offersHoroscopeReading) { this.offersHoroscopeReading = offersHoroscopeReading; }
    }


    // Signup method (now accepts SignupRequest DTO)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        // Check if the email already exists
        if (userRepo.findByEmail(signupRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword()); // Consider hashing passwords!
        user.setRole(signupRequest.getRole());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPhone(signupRequest.getPhone());
        user.setAddressLine1(signupRequest.getAddressLine1());
        user.setAddressLine2(signupRequest.getAddressLine2());
        user.setCity(signupRequest.getCity());
        user.setState(signupRequest.getState());
        user.setZipCode(signupRequest.getZipCode());

        User savedUser = userRepo.save(user); // Save User first to get its ID

        if ("priest".equalsIgnoreCase(signupRequest.getRole())) {
            Priest priest = new Priest();
            priest.setUser(savedUser); // Link priest to the newly saved user
            priest.setBio(signupRequest.getBio());
            priest.setServicesOffered(signupRequest.getServicesOffered());
            priest.setOffersHoroscopeReading(signupRequest.getOffersHoroscopeReading() != null ? signupRequest.getOffersHoroscopeReading() : false);
            priestRepo.save(priest); // Save Priest details
            savedUser.setPriestDetails(priest); // Update user with priest details reference
            userRepo.save(savedUser); // Re-save user to update the relationship
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Login method (now validates by email)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        Optional<User> userOpt = userRepo.findByEmail(creds.get("email")); // Validate by email
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(creds.get("password"))) {
            User user = userOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("email", user.getEmail()); // Send email instead of username
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("role", user.getRole());
            response.put("userId", user.getId());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // Forgot password method (now uses email for validation)
    @PostMapping("/validate-email") // Changed endpoint
    public ResponseEntity<String> validateEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok("Email found");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email"); // Use email
        String newPassword = body.get("newPassword");

        Optional<User> userOpt = userRepo.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword); // Update the password
            userRepo.save(user); // Save updated user
            return ResponseEntity.ok("Password reset successful");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }
    }

    // Update User (adjust to use email for lookup if not by ID, and handle new fields)
    @PutMapping("/updateUser/{userId}") // Added userId path variable for clarity
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody SignupRequest updatedUserRequest) { // Using SignupRequest as DTO
        Optional<User> existingUserOpt = userRepo.findById(userId); // Lookup by ID

        if (!existingUserOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User existingUser = existingUserOpt.get();

        // Update User fields
        existingUser.setFirstName(updatedUserRequest.getFirstName());
        existingUser.setLastName(updatedUserRequest.getLastName());
        existingUser.setPhone(updatedUserRequest.getPhone());
        existingUser.setAddressLine1(updatedUserRequest.getAddressLine1());
        existingUser.setAddressLine2(updatedUserRequest.getAddressLine2());
        existingUser.setCity(updatedUserRequest.getCity());
        existingUser.setState(updatedUserRequest.getState());
        existingUser.setZipCode(updatedUserRequest.getZipCode());
        // Do NOT allow changing role or email directly here if it's sensitive

        // If password is provided in update, update it (consider a separate endpoint for password changes)
        if (updatedUserRequest.getPassword() != null && !updatedUserRequest.getPassword().isEmpty()) {
            existingUser.setPassword(updatedUserRequest.getPassword());
        }

        User savedUser = userRepo.save(existingUser);

        // Update Priest details if applicable
        if ("priest".equalsIgnoreCase(savedUser.getRole())) {
            Optional<Priest> existingPriestOpt = priestRepo.findByUserEmail(savedUser.getEmail());
            Priest priest = existingPriestOpt.orElseGet(() -> {
                Priest newPriest = new Priest();
                newPriest.setUser(savedUser);
                return newPriest;
            });

            priest.setBio(updatedUserRequest.getBio());
            priest.setServicesOffered(updatedUserRequest.getServicesOffered());
            priest.setOffersHoroscopeReading(updatedUserRequest.getOffersHoroscopeReading() != null ? updatedUserRequest.getOffersHoroscopeReading() : false);
            priestRepo.save(priest);
            savedUser.setPriestDetails(priest); // Ensure relationship is set
        }

        return ResponseEntity.ok("User updated successfully");
    }

    // Get all priests with optional filters
    @GetMapping("/priests")
    public ResponseEntity<List<Map<String, Object>>> getPriests(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String poojaType,
            @RequestParam(required = false) Long id) {

        List<User> users = userRepo.findPriestsWithFilters(name, phone, poojaType, id);

        List<Map<String, Object>> priestResponses = users.stream().map(user -> {
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("addressLine1", user.getAddressLine1());
            response.put("addressLine2", user.getAddressLine2());
            response.put("city", user.getCity());
            response.put("state", user.getState());
            response.put("zipCode", user.getZipCode());
            response.put("role", user.getRole());

            if (user.getPriestDetails() != null) {
                response.put("bio", user.getPriestDetails().getBio());
                response.put("servicesOffered", user.getPriestDetails().getServicesOffered());
                response.put("offersHoroscopeReading", user.getPriestDetails().isOffersHoroscopeReading());
            }
            return response;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(priestResponses);
    }

    @DeleteMapping("/priests/{id}")
    public ResponseEntity<?> deletePriest(@PathVariable Long id) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isPresent() && userOpt.get().getRole().equalsIgnoreCase("PRIEST")) {
            userRepo.deleteById(id); // Cascade deletes PriestDetails
            return ResponseEntity.ok("Priest deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Priest not found or not a priest role");
        }
    }

    @PutMapping("/priests/{id}")
    public ResponseEntity<?> updatePriest(@PathVariable Long id, @RequestBody SignupRequest updatedUserRequest) {
        Optional<User> existingUserOpt = userRepo.findById(id);

        if (!existingUserOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Priest not found");
        }

        User existingUser = existingUserOpt.get();

        if (!existingUser.getRole().equalsIgnoreCase("PRIEST")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not a priest");
        }

        // Update User fields
        existingUser.setFirstName(updatedUserRequest.getFirstName());
        existingUser.setLastName(updatedUserRequest.getLastName());
        existingUser.setPhone(updatedUserRequest.getPhone());
        existingUser.setAddressLine1(updatedUserRequest.getAddressLine1());
        existingUser.setAddressLine2(updatedUserRequest.getAddressLine2());
        existingUser.setCity(updatedUserRequest.getCity());
        existingUser.setState(updatedUserRequest.getState());
        existingUser.setZipCode(updatedUserRequest.getZipCode());
        // For password update, handle carefully (e.g., separate endpoint)
        if (updatedUserRequest.getPassword() != null && !updatedUserRequest.getPassword().isEmpty()) {
             existingUser.setPassword(updatedUserRequest.getPassword());
        }

        User savedUser = userRepo.save(existingUser);

        // Update Priest details
        Optional<Priest> existingPriestOpt = priestRepo.findByUserEmail(savedUser.getEmail());
        Priest priest = existingPriestOpt.orElseGet(() -> {
            Priest newPriest = new Priest();
            newPriest.setUser(savedUser);
            return newPriest;
        });

        priest.setBio(updatedUserRequest.getBio());
        priest.setServicesOffered(updatedUserRequest.getServicesOffered());
        priest.setOffersHoroscopeReading(updatedUserRequest.getOffersHoroscopeReading() != null ? updatedUserRequest.getOffersHoroscopeReading() : false);
        priestRepo.save(priest);
        savedUser.setPriestDetails(priest); // Ensure relationship is set
        userRepo.save(savedUser); // Re-save user to update the relationship

        return ResponseEntity.ok("Priest updated successfully");
    }

    @GetMapping("/priests/{id}")
    public ResponseEntity<Map<String, Object>> getPriestById(@PathVariable Long id) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isPresent() && userOpt.get().getRole().equalsIgnoreCase("PRIEST")) {
            User user = userOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("addressLine1", user.getAddressLine1());
            response.put("addressLine2", user.getAddressLine2());
            response.put("city", user.getCity());
            response.put("state", user.getState());
            response.put("zipCode", user.getZipCode());
            response.put("role", user.getRole());

            if (user.getPriestDetails() != null) {
                response.put("bio", user.getPriestDetails().getBio());
                response.put("servicesOffered", user.getPriestDetails().getServicesOffered());
                response.put("offersHoroscopeReading", user.getPriestDetails().isOffersHoroscopeReading());
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Get all customers with optional filters
    @GetMapping("/customers")
    public ResponseEntity<List<User>> getCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Long id) {

        List<User> customers = userRepo.findCustomersWithFilters(name, phone, id);
        return ResponseEntity.ok(customers);
    }

    // Delete a customer by ID
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isPresent() && userOpt.get().getRole().equalsIgnoreCase("CUSTOMER")) {
            userRepo.deleteById(id);
            return ResponseEntity.ok("Customer deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found or not a customer role");
        }
    }

    // Update a customer by ID
    @PutMapping("/customers/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody SignupRequest updatedUserRequest) { // Using SignupRequest as DTO
        Optional<User> existingUserOpt = userRepo.findById(id);

        if (!existingUserOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        User existingUser = existingUserOpt.get();

        if (!existingUser.getRole().equalsIgnoreCase("CUSTOMER")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not a customer");
        }

        // Update fields
        existingUser.setFirstName(updatedUserRequest.getFirstName());
        existingUser.setLastName(updatedUserRequest.getLastName());
        existingUser.setPhone(updatedUserRequest.getPhone());
        existingUser.setAddressLine1(updatedUserRequest.getAddressLine1());
        existingUser.setAddressLine2(updatedUserRequest.getAddressLine2());
        existingUser.setCity(updatedUserRequest.getCity());
        existingUser.setState(updatedUserRequest.getState());
        existingUser.setZipCode(updatedUserRequest.getZipCode());
        // For password update, handle carefully (e.g., separate endpoint)
        if (updatedUserRequest.getPassword() != null && !updatedUserRequest.getPassword().isEmpty()) {
            existingUser.setPassword(updatedUserRequest.getPassword());
        }

        // Customers do not have PriestDetails
        userRepo.save(existingUser);
        return ResponseEntity.ok("Customer updated successfully");
    }

    // Get customer by ID
    @GetMapping("/customers/{id}")
    public ResponseEntity<User> getCustomerById(@PathVariable Long id) {
        Optional<User> customer = userRepo.findById(id);
        if (customer.isPresent() && customer.get().getRole().equalsIgnoreCase("CUSTOMER")) {
            return ResponseEntity.ok(customer.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}