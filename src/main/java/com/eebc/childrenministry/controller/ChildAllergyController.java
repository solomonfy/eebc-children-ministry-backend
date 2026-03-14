package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.ChildAllergyRequest;
import com.eebc.childrenministry.dto.ChildDTO;
import com.eebc.childrenministry.entity.Child;
import com.eebc.childrenministry.entity.ChildAllergy;
import com.eebc.childrenministry.repository.ChildAllergyRepository;
import com.eebc.childrenministry.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChildAllergyController {

    private final ChildAllergyRepository allergyRepo;
    private final ChildRepository childRepo;

    /** GET /children/{childId}/allergies */
    @GetMapping("/children/{childId}/allergies")
    public ResponseEntity<List<ChildDTO.ChildAllergyDTO>> listForChild(@PathVariable String childId) {
        List<ChildDTO.ChildAllergyDTO> dtos = allergyRepo.findByChild_Id(childId)
                .stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    /** POST /children/{childId}/allergies */
    @PostMapping("/children/{childId}/allergies")
    public ResponseEntity<ChildDTO.ChildAllergyDTO> create(
            @PathVariable String childId,
            @RequestBody ChildAllergyRequest req) {
        Child child = childRepo.findById(childId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Child not found"));
        ChildAllergy allergy = new ChildAllergy();
        allergy.setChild(child);
        allergy.setAllergyName(req.allergyName());
        allergy.setSeverity(req.severity());
        allergy.setReaction(req.reaction());
        allergy.setTreatment(req.treatment());
        allergy.setNotes(req.notes());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(allergyRepo.save(allergy)));
    }

    /** PUT /children/allergies/{id} */
    @PutMapping("/children/allergies/{id}")
    public ResponseEntity<ChildDTO.ChildAllergyDTO> update(
            @PathVariable String id,
            @RequestBody ChildAllergyRequest req) {
        ChildAllergy allergy = allergyRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Allergy not found"));
        if (req.allergyName() != null) allergy.setAllergyName(req.allergyName());
        if (req.severity()    != null) allergy.setSeverity(req.severity());
        if (req.reaction()    != null) allergy.setReaction(req.reaction());
        if (req.treatment()   != null) allergy.setTreatment(req.treatment());
        if (req.notes()       != null) allergy.setNotes(req.notes());
        return ResponseEntity.ok(toDTO(allergyRepo.save(allergy)));
    }

    /** DELETE /children/allergies/{id} */
    @DeleteMapping("/children/allergies/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        allergyRepo.deleteById(id);
    }

    private ChildDTO.ChildAllergyDTO toDTO(ChildAllergy a) {
        ChildDTO.ChildAllergyDTO dto = new ChildDTO.ChildAllergyDTO();
        dto.setId(a.getId());
        dto.setAllergyName(a.getAllergyName());
        dto.setSeverity(a.getSeverity());
        dto.setReaction(a.getReaction());
        dto.setTreatment(a.getTreatment());
        dto.setNotes(a.getNotes());
        dto.setCreatedAt(a.getCreatedAt());
        dto.setUpdatedAt(a.getUpdatedAt());
        return dto;
    }
}
