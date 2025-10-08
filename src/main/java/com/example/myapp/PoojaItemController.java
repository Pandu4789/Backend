package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pooja-items")
@CrossOrigin(origins = "http://localhost:3000")
public class PoojaItemController {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private PoojaItemsRepository poojaItemRepo;

    @PostMapping("/add")
    public ResponseEntity<?> addPoojaItemToEvent(@RequestBody PoojaItemsDto dto) {
        Event event = eventRepo.findById(dto.getEventId())
                .orElse(null);

        if (event == null) {
            return ResponseEntity.badRequest().body("Event with ID " + dto.getEventId() + " not found.");
        }

        // Duplicate check logic remains the same
        List<PoojaItems> existingItems = poojaItemRepo.findByEvent_Id(dto.getEventId());
        boolean duplicateExists = existingItems.stream()
                .anyMatch(item -> item.getItemName().equalsIgnoreCase(dto.getItemName()));

        if (duplicateExists) {
            return ResponseEntity.badRequest().body("An item with this name already exists for this event.");
        }

        PoojaItems newItem = new PoojaItems();
        newItem.setItemName(dto.getItemName());
        newItem.setQuantity(dto.getQuantity());
        newItem.setUnitPrice(dto.getUnitPrice());
        newItem.setEvent(event);
        // ✅ CHANGED: Set the unit from the DTO onto the new item entity
        newItem.setUnit(dto.getUnit());

        PoojaItems savedItem = poojaItemRepo.save(newItem);
        return ResponseEntity.ok(savedItem);
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<?> updatePoojaItem(@PathVariable Long itemId, @RequestBody PoojaItemsDto dto) {
        PoojaItems item = poojaItemRepo.findById(itemId)
                .orElse(null);
        
        if (item == null) {
            return ResponseEntity.badRequest().body("Item not found");
        }

        item.setItemName(dto.getItemName());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        // ✅ CHANGED: Set the unit from the DTO when updating the item
        item.setUnit(dto.getUnit());
        
        PoojaItems updatedItem = poojaItemRepo.save(item);
        return ResponseEntity.ok(updatedItem);
    }

    // --- GET and DELETE methods do not require changes ---

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<PoojaItems>> getItemsByEvent(@PathVariable Long eventId) {
        List<PoojaItems> items = poojaItemRepo.findByEvent_Id(eventId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<?> deletePoojaItem(@PathVariable Long itemId) {
        if (!poojaItemRepo.existsById(itemId)) {
            return ResponseEntity.badRequest().body("Item not found");
        }
        poojaItemRepo.deleteById(itemId);
        return ResponseEntity.ok().build();
    }
}

