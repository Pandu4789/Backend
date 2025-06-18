package com.example.myapp;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RestController
@RequestMapping("/api/festivals")
@CrossOrigin(origins = "http://localhost:3000") // Enable for frontend
public class FestivalController {

    private final FestivalRepository repository;

    public FestivalController(FestivalRepository repository) {
        this.repository = repository;
    }
@GetMapping("/month")
    public List<FestivalEntity> getFestivalsByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        return repository.findByDateBetween(startDate, endDate);
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
