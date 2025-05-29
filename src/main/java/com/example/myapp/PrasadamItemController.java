package com.example.myapp;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prasadam")
@RequiredArgsConstructor
public class PrasadamItemController {

    private final PrasadamItemService service;

    // For customers
    @GetMapping("/available")
    public List<PrasadamItemDTO> getAvailableItems() {
        return service.getAvailableItems();
    }

    // For admin
    @GetMapping
    public List<PrasadamItemDTO> getAllItems() {
        return service.getAllItems();
    }

    @PostMapping
    public PrasadamItemDTO createItem(@RequestBody PrasadamItemDTO dto) {
        return service.createItem(dto);
    }

    @PutMapping("/{id}")
    public PrasadamItemDTO updateItem(@PathVariable Long id, @RequestBody PrasadamItemDTO dto) {
        return service.updateItem(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
    }
}
