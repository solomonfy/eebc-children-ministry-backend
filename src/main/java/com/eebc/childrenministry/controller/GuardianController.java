package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Guardian;
import com.eebc.childrenministry.service.GuardianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/guardians")
@RequiredArgsConstructor
public class GuardianController {

    private final GuardianService guardianService;

    @GetMapping
    public ResponseEntity<List<Guardian>> list() {
        return ResponseEntity.ok(guardianService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guardian> getById(@PathVariable String id) {
        return ResponseEntity.ok(guardianService.getGuardianById(id));
    }

    @PostMapping
    public ResponseEntity<Guardian> create(@RequestBody Guardian g) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guardianService.create(g));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Guardian> update(@PathVariable String id, @RequestBody Guardian req) {
        return ResponseEntity.ok(guardianService.update(id, req));
    }

    @PostMapping("/{id}/pin")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<?> setPin(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        guardianService.setPin(id, body.get("pin"));
        return ResponseEntity.ok(Map.of("message", "PIN set successfully"));
    }

    @DeleteMapping("/{id}/pin")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<?> removePin(@PathVariable String id) {
        guardianService.removePin(id);
        return ResponseEntity.ok(Map.of("message", "PIN removed"));
    }

    // Kiosk endpoint — no auth required (public kiosk)
    @PostMapping("/by-pin")
    public ResponseEntity<?> findByPin(@RequestBody Map<String, String> body) {
        return guardianService.findByPin(body.get("pin"))
                .map(g -> ResponseEntity.ok(Map.of(
                        "guardianId", g.getId(),
                        "firstName", g.getFirstName(),
                        "lastName", g.getLastName(),
                        "familyId", g.getFamilyId()
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "PIN not recognized")));
    }
}
