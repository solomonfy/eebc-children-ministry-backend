package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Guardian;
import com.eebc.childrenministry.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/guardians")
@RequiredArgsConstructor
public class GuardianController {

    private final GuardianRepository repository;

    @GetMapping
    public ResponseEntity<List<Guardian>> list() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<Guardian> create(@RequestBody Guardian g) {
        Guardian saved = repository.save(g);
        return ResponseEntity.ok(saved);
    }
}
