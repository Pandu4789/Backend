package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EventRepository eventRepo;  // Repository for fetching events

    // Signup method
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        // Check if the username already exists
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        // If poojas are provided, map them to Event objects
        List<Event> poojas = new ArrayList<>();
        if (user.getPoojas() != null) {
            for (Event pooja : user.getPoojas()) { // Iterate over Event objects
                Optional<Event> eventOpt = eventRepo.findById(pooja.getId());  // Fetch the event by ID
                if (eventOpt.isPresent()) {
                    poojas.add(eventOpt.get());  // Add the Event object to the list
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid pooja ID: " + pooja.getId());
                }
            }
        }

        // Set the poojas (events) for the user
        user.setPoojas(poojas);

        // Save the user
        User savedUser = userRepo.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);  // Respond with the saved user and 201 status
    }

    @GetMapping("/priests")
public ResponseEntity<List<User>> getPriests(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String phone, 
        @RequestParam(required = false) String poojaType,
        @RequestParam(required = false) Long id ) {

    List<User> priests = userRepo.findPriestsWithFilters(name, phone, poojaType, id);
    return ResponseEntity.ok(priests);
}
@DeleteMapping("/priests/{id}")
public ResponseEntity<?> deletePriest(@PathVariable Long id) {
    Optional<User> userOpt = userRepo.findById(id);
    if (userOpt.isPresent() && userOpt.get().getrole().equalsIgnoreCase("PRIEST")) {
        userRepo.deleteById(id);
        return ResponseEntity.ok("Priest deleted successfully");
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Priest not found");
    }
}
@PutMapping("/priests/{id}")
public ResponseEntity<?> updatePriest(@PathVariable Long id, @RequestBody User updatedUser) {
    Optional<User> existingUserOpt = userRepo.findById(id);

    if (!existingUserOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Priest not found");
    }

    User existingUser = existingUserOpt.get();

    // Only update if role is PRIEST
    if (!existingUser.getrole().equalsIgnoreCase("PRIEST")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not a priest");
    }

    // Update fields
    existingUser.setFirstName(updatedUser.getFirstName());
    existingUser.setLastName(updatedUser.getLastName());
    existingUser.setPhone(updatedUser.getPhone());
    existingUser.setAddress(updatedUser.getAddress());
    existingUser.setUsername(updatedUser.getUsername());
    existingUser.setPassword(updatedUser.getPassword());
    
    // Update poojas if provided
    if (updatedUser.getPoojas() != null) {
        List<Event> poojas = new ArrayList<>();
        for (Event pooja : updatedUser.getPoojas()) {
            Optional<Event> eventOpt = eventRepo.findById(pooja.getId());
            eventOpt.ifPresent(poojas::add);
        }
        existingUser.setPoojas(poojas);
    }

    userRepo.save(existingUser);
    return ResponseEntity.ok("Priest updated successfully");
}

@GetMapping("/priests/{id}")
public ResponseEntity<User> getPriestById(@PathVariable Long id) {
    Optional<User> priest = userRepo.findById(id);
    if (priest.isPresent() && priest.get().getrole().equalsIgnoreCase("PRIEST")) {
        return ResponseEntity.ok(priest.get());
    } else {
        return ResponseEntity.notFound().build();
    }
}
    // Login method
    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
    Optional<User> userOpt = userRepo.findByUsername(creds.get("username"));
    if (userOpt.isPresent() && userOpt.get().getPassword().equals(creds.get("password"))) {
        // User is authenticated, prepare the response
        User user = userOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("role", user.getrole());  // Send the role in the response
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}

    

    // Forgot password method
    @PostMapping("/validate-username")
    public ResponseEntity<String> validateUsername(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok("Username found");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Username not found");
        }
    }
    
    @PostMapping("/updateUser")
public ResponseEntity<?> updateUser(@RequestBody User updatedUser) {
    Optional<User> existingUserOpt = userRepo.findByUsername(updatedUser.getUsername());
    if (!existingUserOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    User existingUser = existingUserOpt.get();
    existingUser.setFirstName(updatedUser.getFirstName());
    existingUser.setLastName(updatedUser.getLastName());
    existingUser.setPhone(updatedUser.getPhone());
    existingUser.setAddress(updatedUser.getAddress());
    existingUser.setPassword(updatedUser.getPassword());
    existingUser.setUsername(updatedUser.getUsername()); // In case username is editable

    userRepo.save(existingUser);
    return ResponseEntity.ok("User updated successfully");
}

    @PostMapping("/reset-password")
public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
    String username = body.get("username");
    String newPassword = body.get("newPassword");

    Optional<User> userOpt = userRepo.findByUsername(username);

    if (userOpt.isPresent()) {
        User user = userOpt.get();
        user.setPassword(newPassword); // Update the password
        userRepo.save(user); // Save updated user
        return ResponseEntity.ok("Password reset successful");
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Username not found");
    }
}

    
}
