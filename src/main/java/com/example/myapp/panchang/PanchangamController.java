package com.example.myapp.panchang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/panchangam")
@CrossOrigin(origins = "http://localhost:3000")
public class PanchangamController {

    @Autowired
    private PanchangamRepository repository;

    // ✅ Create new Panchangam
    @PostMapping
    public Panchangam save(@RequestBody Panchangam panchangam) {
        return repository.save(panchangam);
    }

    // ✅ Get all Panchangam entries
    @GetMapping
    public List<Panchangam> getAll() {
        return repository.findAll();
    }

    // ✅ Get by Date (optional for editing frontend)
    @GetMapping("/{date}")
    public Optional<Panchangam> getByDate(@PathVariable java.time.LocalDate date) {
        return repository.findById(date);
    }

    // ✅ Update existing Panchangam
    @PutMapping("/{id}")
    public Panchangam update(@PathVariable LocalDate id, @RequestBody Panchangam updated) {
        return repository.findById(id).map(p -> {
            p.setDate(updated.getDate());
            p.setMuhurtamLagna(updated.getMuhurtamLagna());
            p.setNakshatram(updated.getNakshatram());
            p.setPaksham(updated.getPaksham());
            p.setTithi(updated.getTithi());
            p.setVara(updated.getVara());
            p.setMuhurtamTime(updated.getMuhurtamTime());
            p.setNotes(updated.getNotes());
            return repository.save(p);
        }).orElseThrow(() -> new RuntimeException("Panchangam not found with id " + id));
    }

    // ✅ Delete Panchangam
    @DeleteMapping("/{id}")
    public void delete(@PathVariable LocalDate id) {
        repository.deleteById(id);
    }

    // ✅ Get by Nakshatram (already present)
    @GetMapping("/by-nakshatram/{name}")
    public List<Panchangam> getByNakshatram(@PathVariable String name) {
        return repository.findByNakshatram(name);
    }
}
