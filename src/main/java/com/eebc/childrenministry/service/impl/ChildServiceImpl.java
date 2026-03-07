package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.dto.ChildDTO;
import com.eebc.childrenministry.entity.Child;
import com.eebc.childrenministry.entity.ChildAllergy;
import com.eebc.childrenministry.repository.ChildRepository;
import com.eebc.childrenministry.repository.FamilyRepository;
import com.eebc.childrenministry.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChildServiceImpl implements ChildService {

    private final ChildRepository childRepository;
    private final FamilyRepository familyRepository;

    private static final Logger logger = LoggerFactory.getLogger(ChildServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<ChildDTO> getAllChildren() {
        try {
            List<ChildDTO> children = childRepository.findAllWithAllergies()
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} children from the repository.", children.size());
            return children;
        } catch (Exception e) {
            logger.error("Error retrieving children: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChildDTO> getChildrenByFamilyId(String familyId) {
        try {
            if (familyRepository.findById(familyId).isEmpty()) {
                logger.warn("No family found with ID: {}", familyId);
                return List.of();
            }
            List<ChildDTO> children = childRepository.findByFamilyId(familyId)
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            logger.info("Retrieved {} children for family ID: {}", children.size(), familyId);
            return children;
        } catch (Exception e) {
            logger.error("Error retrieving children for family ID {}: {}", familyId, e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChildDTO> getChildById(String id) {
        try {
            return childRepository.findByIdWithAllergies(id)
                    .map(this::toDTO);
        } catch (Exception e) {
            logger.error("Error retrieving child with ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChildDTO> getChildByLastName(String lastName) {
        try {
            List<Child> children = childRepository.findAllWithAllergies()
                    .stream()
                    .filter(c -> c.getLastName().equalsIgnoreCase(lastName))
                    .collect(Collectors.toList());

            if (children.isEmpty()) {
                logger.warn("No child found with last name: {}", lastName);
                return Optional.empty();
            }
            if (children.size() > 1) {
                logger.warn("Multiple children found with last name: {}. Returning the first match.", lastName);
            }
            return Optional.of(toDTO(children.get(0)));
        } catch (Exception e) {
            logger.error("Error retrieving child with last name {}: {}", lastName, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public ChildDTO createChild(Child child) {
        Child saved = childRepository.save(child);
        return toDTO(saved);
    }

    private ChildDTO toDTO(Child child) {
        ChildDTO dto = new ChildDTO();
        dto.setId(child.getId());
        dto.setFirstName(child.getFirstName());
        dto.setLastName(child.getLastName());
        dto.setBirthDate(child.getBirthDate());

        List<ChildDTO.ChildAllergyDTO> allergies = child.getAllergies()
                .stream()
                .map(this::toAllergyDTO)
                .collect(Collectors.toList());
        dto.setAllergies(allergies);
        return dto;
    }

    private ChildDTO.ChildAllergyDTO toAllergyDTO(ChildAllergy allergy) {
        ChildDTO.ChildAllergyDTO dto = new ChildDTO.ChildAllergyDTO();
        dto.setId(allergy.getId());
        dto.setAllergyName(allergy.getAllergyName());
        dto.setSeverity(allergy.getSeverity());
        dto.setReaction(allergy.getReaction());
        dto.setTreatment(allergy.getTreatment());
        dto.setNotes(allergy.getNotes());
        return dto;
    }
}