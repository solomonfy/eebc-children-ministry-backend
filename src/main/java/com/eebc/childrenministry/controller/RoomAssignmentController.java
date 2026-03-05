package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.RoomAssignment;
import com.eebc.childrenministry.repository.RoomAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rooms/assignments")
@RequiredArgsConstructor
public class RoomAssignmentController {

    private final RoomAssignmentRepository repository;

    @GetMapping
    public ResponseEntity<List<RoomAssignment>> list() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<RoomAssignment> create(@RequestBody RoomAssignment r) {
        RoomAssignment saved = repository.save(r);
        return ResponseEntity.ok(saved);
    }
}
