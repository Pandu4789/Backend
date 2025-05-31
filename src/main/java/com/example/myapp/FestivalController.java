package com.example.myapp;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/festivals")
@CrossOrigin(origins = "http://localhost:3000") // Enable for frontend
public class FestivalController {

    private final FestivalRepository repository;

    public FestivalController(FestivalRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<FestivalEntity> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public FestivalEntity create(@RequestBody FestivalEntity festival) {
        return repository.save(festival);
    }

    @PutMapping("/{id}")
    public FestivalEntity update(@PathVariable Long id, @RequestBody FestivalEntity updated) {
        FestivalEntity existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Festival not found"));
        existing.setName(updated.getName());
        existing.setDate(updated.getDate());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
