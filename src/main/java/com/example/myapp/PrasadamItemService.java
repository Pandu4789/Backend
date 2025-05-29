package com.example.myapp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrasadamItemService {

    private final PrasadamItemRepository repository;

    public List<PrasadamItemDTO> getAllItems() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PrasadamItemDTO> getAvailableItems() {
        return repository.findByAvailableTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PrasadamItemDTO createItem(PrasadamItemDTO dto) {
        PrasadamItem item = toEntity(dto);
        return toDTO(repository.save(item));
    }

    public PrasadamItemDTO updateItem(Long id, PrasadamItemDTO dto) {
        PrasadamItem item = repository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        item.setName(dto.getName());
        item.setCategory(dto.getCategory());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setImageUrl(dto.getImageUrl());
        item.setAvailable(dto.isAvailable());
        return toDTO(repository.save(item));
    }

    public void deleteItem(Long id) {
        repository.deleteById(id);
    }

    private PrasadamItemDTO toDTO(PrasadamItem item) {
        return PrasadamItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .category(item.getCategory())
                .price(item.getPrice())
                .description(item.getDescription())
                .imageUrl(item.getImageUrl())
                .available(item.isAvailable())
                .build();
    }

    private PrasadamItem toEntity(PrasadamItemDTO dto) {
        return PrasadamItem.builder()
                .id(dto.getId())
                .name(dto.getName())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .available(dto.isAvailable())
                .build();
    }
}
