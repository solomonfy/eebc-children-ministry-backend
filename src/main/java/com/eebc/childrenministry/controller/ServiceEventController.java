package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.ServiceEvent;
import com.eebc.childrenministry.repository.ServiceEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceEventController {

    private final ServiceEventRepository repository;

    @GetMapping
    public ResponseEntity<List<ServiceEvent>> list() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<ServiceEvent> create(@RequestBody ServiceEvent s) {
        ServiceEvent saved = repository.save(s);
        return ResponseEntity.ok(saved);
    }
}
