package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.ChildDTO;
import com.eebc.childrenministry.dto.RegisterChildRequest;
import com.eebc.childrenministry.entity.Child;
import com.eebc.childrenministry.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/children")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    @GetMapping
    public ResponseEntity<List<ChildDTO>> list() {
        return ResponseEntity.ok(childService.getAllChildren());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChildDTO> getChildById(@PathVariable String id) {
        return childService.getChildById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<ChildDTO>> getByFamilyId(@PathVariable String familyId) {
        List<ChildDTO> children = childService.getChildrenByFamilyId(familyId);
        if (children == null || children.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(children);
    }

    @PostMapping
    public ResponseEntity<ChildDTO> createChild(@RequestBody Child child) {
        ChildDTO created = childService.createChild(child);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChildDTO> update(@PathVariable String id, @RequestBody RegisterChildRequest req) {
        ChildDTO updated = childService.updateChild(id, req);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

//    // Verify PIN at pickup — used by kiosk
//    @PostMapping("/{id}/verify-pin")
//    public ResponseEntity<Map<String, Boolean>> verifyPin(
//            @PathVariable String id,
//            @RequestBody Map<String, String> body) {
//        boolean valid = childService.verifyPickupPin(id, body.get("pin"));
//        return ResponseEntity.ok(Map.of("valid", valid));
//    }


}