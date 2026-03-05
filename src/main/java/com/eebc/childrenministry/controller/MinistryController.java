package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Ministry;
import com.eebc.childrenministry.repository.MinistryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ministries")
@RequiredArgsConstructor
public class MinistryController {

    private final MinistryRepository repository;

    @GetMapping
    public ResponseEntity<List<Ministry>> list() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<Ministry> create(@RequestBody Ministry m) {
        Ministry saved = repository.save(m);
        return ResponseEntity.ok(saved);
    }
}
