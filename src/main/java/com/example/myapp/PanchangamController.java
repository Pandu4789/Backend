package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PanchangamController {

    @Autowired
    private PanchangamRepository panchangamRepository;

    // Endpoint to get Panchangam data by Nakshatram name
    @GetMapping("/api/panchangam/by-nakshatram/{name}")
    public List<Panchangam> getByNakshatram(@PathVariable String name) {
        return panchangamRepository.findByNakshatram(name);
    }
}
