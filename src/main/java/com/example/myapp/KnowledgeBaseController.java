package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/knowledgebase")
@CrossOrigin(origins = "http://localhost:3000")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseRepository knowledgeBaseRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{priestId}")
    public ResponseEntity<String> getKnowledgeBase(@PathVariable Long priestId) {
        Optional<KnowledgeBaseEntry> kb = knowledgeBaseRepository.findByPriestId(priestId);
        String content = kb.map(KnowledgeBaseEntry::getContent)
                           .orElse("<h2>Welcome to your Knowledge Base!</h2><p>Click the 'Edit' button to start writing your personal notes and mantras.</p>");
        return ResponseEntity.ok(content);
    }

    @PostMapping
    public ResponseEntity<KnowledgeBaseEntry> saveKnowledgeBase(@RequestBody KnowledgeBaseDto dto) {
        User priest = userRepository.findById(dto.getPriestId())
                .orElseThrow(() -> new RuntimeException("Priest not found with ID: " + dto.getPriestId()));

        KnowledgeBaseEntry kb = knowledgeBaseRepository.findByPriestId(dto.getPriestId())
                .orElse(new KnowledgeBaseEntry());

        kb.setPriest(priest);
        kb.setContent(dto.getContent());

        KnowledgeBaseEntry savedKb = knowledgeBaseRepository.save(kb);
        return ResponseEntity.ok(savedKb);
    }
}