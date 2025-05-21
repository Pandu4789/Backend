package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/pooja-items")
@CrossOrigin(origins = "http://localhost:3000")
public class PoojaItemController {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private PoojaItemsRepository poojaItemRepo;

    // ✅ ADD ITEM TO EVENT (With duplicate name check per event)
    @PostMapping("/add")
    public ResponseEntity<?> addPoojaItemToEvent(@RequestBody PoojaItemsDto dto) {
        Optional<Event> eventOpt = eventRepo.findById(dto.getEventId());
        if (eventOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Event not found");
        }

        Event event = eventOpt.get();

        // Check for duplicate item (case-insensitive) in this event
        List<PoojaItems> existingItems = poojaItemRepo.findByEvents_Id(dto.getEventId());
        boolean duplicateExists = existingItems.stream()
                .anyMatch(item -> item.getItemName().equalsIgnoreCase(dto.getItemName()));

        if (duplicateExists) {
            return ResponseEntity.badRequest().body("Duplicate item name not allowed for this event");
        }

        PoojaItems item = new PoojaItems();
        item.setItemName(dto.getItemName());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());

        Set<Event> events = new HashSet<>();
        events.add(event);
        item.setEvents(events);

        poojaItemRepo.save(item);

        return ResponseEntity.ok(item);
    }

    // ✅ GET ITEMS BY EVENT
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<PoojaItems>> getItemsByEvent(@PathVariable Long eventId) {
        List<PoojaItems> items = poojaItemRepo.findByEvents_Id(eventId);
        return ResponseEntity.ok(items);
    }

    // ✅ UPDATE ITEM (Edit functionality)
    @PutMapping("/update/{itemId}")
    public ResponseEntity<?> updatePoojaItem(@PathVariable Long itemId, @RequestBody PoojaItemsDto dto) {
        Optional<PoojaItems> itemOpt = poojaItemRepo.findById(itemId);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Item not found");
        }

        PoojaItems item = itemOpt.get();

        // Only update name if not duplicate within the same event
        Long eventId = dto.getEventId();
        if (eventId != null) {
            List<PoojaItems> items = poojaItemRepo.findByEvents_Id(eventId);
            boolean duplicateExists = items.stream()
                    .anyMatch(i -> i.getItemName().equalsIgnoreCase(dto.getItemName()) && !i.getId().equals(itemId));

            if (duplicateExists) {
                return ResponseEntity.badRequest().body("Duplicate item name not allowed");
            }
        }

        item.setItemName(dto.getItemName());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());

        poojaItemRepo.save(item);
        return ResponseEntity.ok(item);
    }

    // ✅ DELETE ITEM
    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<?> deletePoojaItem(@PathVariable Long itemId) {
        if (!poojaItemRepo.existsById(itemId)) {
            return ResponseEntity.badRequest().body("Item not found");
        }

        poojaItemRepo.deleteById(itemId);
        return ResponseEntity.ok().build();
    }
}
