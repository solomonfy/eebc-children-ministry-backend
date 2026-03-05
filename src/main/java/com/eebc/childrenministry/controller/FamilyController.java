package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Family;
import com.eebc.childrenministry.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyRepository repository;

    @GetMapping
    public ResponseEntity<List<Family>> list() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Family> getById(@RequestBody String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Family> create(@RequestBody Family f) {
        Family saved = repository.save(f);
        return ResponseEntity.ok(saved);
    }
}
