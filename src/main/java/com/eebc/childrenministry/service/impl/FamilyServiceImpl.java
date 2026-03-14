package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.config.RequestContext;
import com.eebc.childrenministry.entity.Family;
import com.eebc.childrenministry.repository.FamilyRepository;
import com.eebc.childrenministry.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    @Autowired
    FamilyRepository familyRepository;

    @Autowired
    RequestContext requestContext;

    private static final Logger logger = LoggerFactory.getLogger(FamilyServiceImpl.class);

    private boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(requestContext.getRole());
    }

    @Override
    public List<Family> getAllFamilies() {
        try {
            List<Family> familyList = isSuperAdmin()
                    ? familyRepository.findAll()
                    : familyRepository.findAllByCampusId(requestContext.getCampusId());
            logger.info("Retrieved {} families.", familyList.size());
            return familyList;
        } catch (Exception e) {
            logger.error("Error retrieving families: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Family> getFamilyById(String id) {
        try {
            Optional<Family> family = isSuperAdmin()
                    ? familyRepository.findById(id)
                    : familyRepository.findByIdAndCampusId(id, requestContext.getCampusId());
            if (family.isEmpty()) {
                logger.warn("No family found with ID: {}", id);
            } else {
                logger.info("Retrieved family with ID: {}", id);
            }
            return family;
        } catch (Exception e) {
            logger.error("Error retrieving family with ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Family> getFamilyByName(String name) {
        try {
            Optional<Family> family = familyRepository.findByLastName(name);
            if (family == null) {
                logger.warn("No family found with name: {}", name);
                return Optional.empty();
            }
            logger.info("Retrieved family: {} with name: {}", family, name);
            return family;
        } catch (Exception e) {
            logger.error("Error retrieving family with name {}: {}", name, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Family> getFamilyByPin(String pin) {
        return Optional.empty();
    }

    @Override
    public Family createFamily(Family family) {
        try {
            if (!isSuperAdmin()) family.setCampusId(requestContext.getCampusId());
            if (family.getStatus() == null) family.setStatus("ACTIVE");
            Family saved = familyRepository.save(family);
            logger.info("Created family with ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Error creating family: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Family updateFamily(String id, Family req) {
        try {
            Family existing = (isSuperAdmin()
                    ? familyRepository.findById(id)
                    : familyRepository.findByIdAndCampusId(id, requestContext.getCampusId()))
                    .orElseThrow(() -> new RuntimeException("Family not found: " + id));
            if (req.getLastName() != null)                     existing.setLastName(req.getLastName());
            if (req.getPhone() != null)                        existing.setPhone(req.getPhone());
            if (req.getEmail() != null)                        existing.setEmail(req.getEmail());
            if (req.getEmergency_contact_name() != null)       existing.setEmergency_contact_name(req.getEmergency_contact_name());
            if (req.getEmergency_contact_relationship() != null) existing.setEmergency_contact_relationship(req.getEmergency_contact_relationship());
            if (req.getNotes() != null)                        existing.setNotes(req.getNotes());
            if (req.getCommunication_method() != null)         existing.setCommunication_method(req.getCommunication_method());
            if (req.getFirst_visit_date() != null)             existing.setFirst_visit_date(req.getFirst_visit_date());
            if (req.getStatus() != null)                       existing.setStatus(req.getStatus());
            existing.setUpdated_at(java.time.LocalDateTime.now());
            Family saved = familyRepository.save(existing);
            logger.info("Updated family with ID: {}", id);
            return saved;
        } catch (Exception e) {
            logger.error("Error updating family with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }
}
