package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "http://localhost:3000")
public class PriestAvailabilityController {

    @Autowired private PriestAvailabilityRepository availabilityRepository;
    @Autowired private UserRepository userRepository;

    // GET all unavailable slots for a priest
    @GetMapping("/{priestId}")
public List<PriestAvailabilityResponseDto> getAvailability(@PathVariable Long priestId) {
    List<PriestAvailability> availabilityEntities = availabilityRepository.findByPriestId(priestId);

    // Convert the list of entities to a list of DTOs before returning
    return availabilityEntities.stream()
            .map(PriestAvailabilityResponseDto::new) // Creates a new DTO for each entity
            .collect(Collectors.toList());
}

    // POST to save a full day's schedule of unavailable slots
    @PostMapping
    @Transactional
    public ResponseEntity<?> saveAvailability(@RequestBody PriestAvailabilityDto dto) {
        User priest = userRepository.findById(dto.getPriestId())
                .orElseThrow(() -> new RuntimeException("Priest not found"));
        
        LocalDate date = LocalDate.parse(dto.getDate());

        // Step 1: Delete all existing unavailable slots for this priest on this day.
        availabilityRepository.deleteByPriestIdAndSlotDate(dto.getPriestId(), date);

        // Step 2: Save the new list of unavailable slots.
        for (String timeStr : dto.getUnavailableSlots()) {
            PriestAvailability newSlot = PriestAvailability.builder()
                    .priest(priest)
                    .slotDate(date)
                    .slotTime(LocalTime.parse(timeStr))
                    .build();
            availabilityRepository.save(newSlot);
        }

        return ResponseEntity.ok("Availability updated successfully for " + dto.getDate());
    }
}