package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Volunteer;
import com.eebc.childrenministry.service.VolunteerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteers")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;

    @GetMapping
    public ResponseEntity<List<Volunteer>> getAll() {
        return ResponseEntity.ok(volunteerService.getAllVolunteers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Volunteer> getById(@PathVariable String id) {
        return volunteerService.getVolunteerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Volunteer> create(@RequestBody Volunteer volunteer) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(volunteerService.createVolunteer(volunteer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Volunteer> update(@PathVariable String id, @RequestBody Volunteer volunteer) {
        return ResponseEntity.ok(volunteerService.updateVolunteer(id, volunteer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        volunteerService.deleteVolunteer(id);
        return ResponseEntity.noContent().build();
    }

}
