package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "http://localhost:3000")
public class PriestAvailabilityController {

    @Autowired private PriestAvailabilityRepository availabilityRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AppointmentBookingRepository bookingRepository;
    @Autowired private AppointmentRepository manualAppointmentRepository;


    // GET all unavailable slots for a priest
    @GetMapping("/{priestId}")
public List<PriestAvailabilityResponseDto> getAvailability(@PathVariable Long priestId) {
    List<PriestAvailability> availabilityEntities = availabilityRepository.findByPriestId(priestId);

    // Convert the list of entities to a list of DTOs before returning
    return availabilityEntities.stream()
            .map(PriestAvailabilityResponseDto::new) // Creates a new DTO for each entity
            .collect(Collectors.toList());
}
 @GetMapping("/priest/{priestId}/date/{date}")
    public ResponseEntity<List<String>> getAvailableSlotsForDay(
            @PathVariable Long priestId,
            @PathVariable String date) {

        LocalDate localDate = LocalDate.parse(date);

        // 1. Get all possible time slots for a day
        List<LocalTime> allSlots = IntStream.rangeClosed(3, 23)
                .mapToObj(hour -> LocalTime.of(hour, 0))
                .collect(Collectors.toList());

        // 2. Find slots already marked as "Unavailable" by the priest FOR THAT SPECIFIC DAY
        List<LocalTime> unavailableTimes = availabilityRepository.findByPriestIdAndSlotDate(priestId, localDate).stream()
                .map(PriestAvailability::getSlotTime)
                .collect(Collectors.toList());

        // 3. Find slots taken by customer bookings FOR THAT SPECIFIC DAY
        List<LocalTime> bookedTimes = bookingRepository.findByPriestIdAndDate(priestId, date).stream()
                .filter(b -> "ACCEPTED".equalsIgnoreCase(b.getStatus()) || "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                .map(b -> LocalTime.parse(b.getStart()))
                .collect(Collectors.toList());
        
        // 4. Find slots taken by manual appointments FOR THAT SPECIFIC DAY
        List<LocalTime> manualAppointmentTimes = manualAppointmentRepository.findByPriestIdAndStartBetween(priestId, localDate.atStartOfDay(), localDate.atTime(23, 59, 59)).stream()
                .map(a -> a.getStart().toLocalTime())
                .collect(Collectors.toList());

        // 5. Combine all taken slots into one list
        List<LocalTime> allTakenSlots = new ArrayList<>();
        allTakenSlots.addAll(unavailableTimes);
        allTakenSlots.addAll(bookedTimes);
        allTakenSlots.addAll(manualAppointmentTimes);

        // 6. Filter the full list of slots to find only the truly available ones
        List<String> availableSlots = allSlots.stream()
                .filter(slot -> !allTakenSlots.contains(slot))
                .map(slot -> slot.format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(availableSlots);
    }
@GetMapping("/priest/{priestId}/available-dates")
public ResponseEntity<List<String>> getAvailableDatesInMonth(
        @PathVariable Long priestId,
        @RequestParam int year,
        @RequestParam int month) {

    YearMonth yearMonth = YearMonth.of(year, month);
    LocalDate startDate = yearMonth.atDay(1);
    LocalDate endDate = yearMonth.atEndOfMonth();

    // Use the existing service method to calculate slots for each day
    // This is just an example; you would build a dedicated service method for this in a real app
    List<String> availableDates = new ArrayList<>();
    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
        ResponseEntity<List<String>> slotsResponse = getAvailableSlotsForDay(priestId, date.toString());
        if (slotsResponse.getBody() != null && !slotsResponse.getBody().isEmpty()) {
            availableDates.add(date.toString());
        }
    }
    
    return ResponseEntity.ok(availableDates);
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