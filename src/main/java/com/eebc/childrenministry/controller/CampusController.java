package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Campus;
import com.eebc.childrenministry.repository.CampusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/campuses")
@RequiredArgsConstructor
public class CampusController {

    private final CampusRepository campusRepository;

    @GetMapping
    public ResponseEntity<List<Campus>> list() {
        return ResponseEntity.ok(campusRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campus> getById(@RequestBody String id) {
        return campusRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Campus> create(@RequestBody Campus c) {
        Campus saved = campusRepository.save(c);
        return ResponseEntity.ok(saved);
    }
}
