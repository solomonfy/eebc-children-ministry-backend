package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.ChildAllergy;
import com.eebc.childrenministry.repository.ChildAllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/children/allergies")
@RequiredArgsConstructor
public class ChildAllergyController {

    private final ChildAllergyRepository repository;

    @GetMapping
    public ResponseEntity<List<ChildAllergy>> list() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<ChildAllergy> create(@RequestBody ChildAllergy a) {
        ChildAllergy saved = repository.save(a);
        return ResponseEntity.ok(saved);
    }
}
