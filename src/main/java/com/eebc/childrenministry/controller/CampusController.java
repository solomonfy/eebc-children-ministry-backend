package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.config.RequestContext;
import com.eebc.childrenministry.entity.Campus;
import com.eebc.childrenministry.repository.CampusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campuses")
@RequiredArgsConstructor
public class CampusController {

    private final CampusRepository campusRepository;
    private final RequestContext requestContext;

    @GetMapping
    public ResponseEntity<List<Campus>> list() {
        return ResponseEntity.ok(campusRepository.findAll());
    }

    @GetMapping("/current")
    public ResponseEntity<Campus> getCurrent() {
        String campusId = requestContext.getCampusId();
        if (campusId == null) return ResponseEntity.notFound().build();
        return campusRepository.findById(campusId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campus> getById(@PathVariable String id) {
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
