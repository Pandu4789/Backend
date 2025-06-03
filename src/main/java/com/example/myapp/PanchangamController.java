package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    // ✅ Get by ID (optional for editing frontend)
    @GetMapping("/{id}")
    public Optional<Panchangam> getById(@PathVariable Long id) {
        return repository.findById(id);
    }

    // ✅ Update existing Panchangam
    @PutMapping("/{id}")
    public Panchangam update(@PathVariable Long id, @RequestBody Panchangam updated) {
        return repository.findById(id).map(p -> {
            p.setDate(updated.getDate());
            p.setLagnam(updated.getLagnam());
            p.setMohurtam(updated.getMohurtam());
            p.setNakshatram(updated.getNakshatram());
            p.setPaksha(updated.getPaksha());
            p.setTithi(updated.getTithi());
            p.setVaaram(updated.getVaaram());
            p.setTime(updated.getTime());
            p.setNotes(updated.getNotes());
            return repository.save(p);
        }).orElseThrow(() -> new RuntimeException("Panchangam not found with id " + id));
    }

    // ✅ Delete Panchangam
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

    // ✅ Get by Nakshatram (already present)
    @GetMapping("/by-nakshatram/{name}")
    public List<Panchangam> getByNakshatram(@PathVariable String name) {
        return repository.findByNakshatram(name);
    }
}
