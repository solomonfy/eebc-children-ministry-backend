package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Family;
import com.eebc.childrenministry.repository.FamilyRepository;
import com.eebc.childrenministry.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @GetMapping
    public ResponseEntity<List<Family>> list() {
        return ResponseEntity.ok(familyService.getAllFamilies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Family>> getById(@RequestBody String id) {
        return familyService.getFamilyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-pin/{pin}")
    public ResponseEntity<?> getByPin(@PathVariable String pin) {
        return familyService.getFamilyByPin(pin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Family> create(@RequestBody Family f) {
        Family saved = familyService.createFamily(f);
        return ResponseEntity.ok(saved);
    }
}
