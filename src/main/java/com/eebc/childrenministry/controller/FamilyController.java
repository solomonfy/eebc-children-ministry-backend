package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Family;
import com.eebc.childrenministry.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Family> getById(@PathVariable String id) {
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
        return ResponseEntity.status(HttpStatus.CREATED).body(familyService.createFamily(f));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Family> update(@PathVariable String id, @RequestBody Family req) {
        return ResponseEntity.ok(familyService.updateFamily(id, req));
    }
}
