package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/gallery")
@CrossOrigin(origins = "http://localhost:3000")
public class GalleryController {

    @Autowired
    private GalleryImageRepository galleryRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Endpoint for priests to upload an image
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @RequestParam("caption") String caption,
                                         @RequestParam("priestId") Long priestId,
                                         @RequestParam("priestName") String priestName) {
        // --- Enforce 10 images per month rule ---
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        long monthlyUploads = galleryRepository.countByPriestIdAndUploadDateBetween(priestId, startOfMonth, endOfMonth);

        if (monthlyUploads >= 10) {
            return ResponseEntity.status(429).body(Map.of("error", "Monthly upload limit of 10 images reached."));
        }
        // --- End rule enforcement ---

        // Generate a unique filename
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        try {
            // Save the file to the server's filesystem
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            // Create and save the entity to the database
            GalleryImage image = new GalleryImage();
            image.setImageUrl("/images/" + fileName); // The web-accessible URL
            image.setCaption(caption);
            image.setPriestId(priestId);
            image.setPriestName(priestName);
            image.setUploadDate(LocalDate.now());

            GalleryImage savedImage = galleryRepository.save(image);
            return ResponseEntity.ok(savedImage);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload file."));
        }
    }

    // Endpoint to get all images for a specific priest (for their gallery page)
    @GetMapping("/priest/{priestId}")
    public List<GalleryImage> getImagesByPriest(@PathVariable Long priestId) {
        return galleryRepository.findByPriestId(priestId);
    }

    // Endpoint to get a random list of all images for the customer dashboard
    @GetMapping("/all-random")
    public List<GalleryImage> getAllImagesRandomly() {
        List<GalleryImage> allImages = galleryRepository.findAll();
        Collections.shuffle(allImages); // Shuffle the list randomly
        return allImages;
    }

    // Endpoint for a priest to delete their own image
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        // In a real app, you'd also check if the logged-in user owns this image
        galleryRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Image deleted successfully."));
    }
}