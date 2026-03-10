package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Church;
import com.eebc.childrenministry.repository.ChurchRepository;
import com.eebc.childrenministry.service.ChurchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/churches")
@RequiredArgsConstructor
public class ChurchController {


    @Autowired
    private ChurchService churchService;

    @GetMapping
    public ResponseEntity<List<Church>> getAllChurches(Authentication authentication) {
        // User is authenticated if this endpoint is called
        List<Church> churches = churchService.getAllChurches();
        return ResponseEntity.ok(churches);
    }

    @GetMapping("/{id}")
    public Optional<Church> getChurchById(@PathVariable String id) {
        Optional<Church> church = churchService.getChurchById(id);
        return ResponseEntity.ok(church).getBody();
    }

    @PostMapping
    public ResponseEntity<Church> createChurch(@RequestBody Church church) {
        Church saved = churchService.saveChurch(church);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Backend is running!");
    }
}
