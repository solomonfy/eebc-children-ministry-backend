package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.RegisterChildRequest;
import com.eebc.childrenministry.entity.Child;
import com.eebc.childrenministry.repository.ChildRepository;
import com.eebc.childrenministry.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/children")
@RequiredArgsConstructor
public class ChildController {

    private final ChildRepository repository;

    @GetMapping
    public ResponseEntity<List<Child>> list() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Child> getById(@RequestBody String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<Child>> getByFamilyId(@RequestBody String familyId) {
        List<Child> children = repository.findByFamilyId(familyId);
        if (children == null || children.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(children);
    }

//    @PostMapping
//    public ResponseEntity<Child> registerChild(@RequestBody RegisterChildRequest req) {
//        Child child = new Child();
//        child.setFamilyId(req.familyId());
//        child.setFirstName(req.firstName());
//        child.setLastName(req.lastName());
//        child.setNickname(req.nickname());
//        child.setBirthDate(req.birthDate());
//        child.setGender(req.gender());
//        child.setGrade(req.grade());
//        child.setSpecialNeeds(req.specialNeeds());
//        child.setNotes(req.notes());
//        child.setStatus("ACTIVE");
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(repository.save(child));
//    }
//
//    // Verify PIN at pickup — used by kiosk
//    @PostMapping("/{id}/verify-pin")
//    public ResponseEntity<Map<String, Boolean>> verifyPin(
//            @PathVariable String id,
//            @RequestBody Map<String, String> body) {
//        boolean valid = childService.verifyPickupPin(id, body.get("pin"));
//        return ResponseEntity.ok(Map.of("valid", valid));
//    }
}
