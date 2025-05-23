package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/nakshatram")
public class NakshatramController {

    @Autowired
    private NakshatramRepository nakshatramRepository;

    @GetMapping
    public List<Nakshatram> getAllNakshatrams() {
        return nakshatramRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
