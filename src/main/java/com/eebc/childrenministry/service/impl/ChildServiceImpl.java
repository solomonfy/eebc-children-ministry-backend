package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.entity.Child;
import com.eebc.childrenministry.entity.Family;
import com.eebc.childrenministry.repository.ChildRepository;
import com.eebc.childrenministry.repository.FamilyRepository;
import com.eebc.childrenministry.service.ChildService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChildServiceImpl implements ChildService {
    @Autowired
    ChildRepository childRepository;

    @Autowired
    FamilyRepository familyRepository;

    private static final Logger logger = LoggerFactory.getLogger(ChurchServiceImpl.class);

    @Autowired
    private BCryptPasswordEncoder encoder;  // reuse the same bean from SecurityConfig

//    public Child registerChild(Child child, String rawPin) {
//        if (rawPin != null && !rawPin.isBlank()) {
//            child.setPickUpPinHash(encoder.encode(rawPin));
//        }
//        return childRepository.save(child);
//    }
//
//    public boolean verifyPickupPin(String childId, String rawPin) {
//        Child child = childRepository.findById(childId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Child not found"));
//        if (child.getPickUpPinHash() == null) return false;
//        return encoder.matches(rawPin, child.getPickUpPinHash());
//    }

    @Override
    public List<Child> getAllChildren() {
        List<Child> childList = new ArrayList<>();
        try {
            childList = childRepository.findAll();
            logger.info("Retrieved {} children from the repository.", childList);
            return childList;
        } catch (Exception e) {
            logger.error("Error retrieving children from the repository: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Child> getChildrenByFamilyId(String familyId) {
        try {
            Optional<Family> family = familyRepository.findById(familyId);
            if (family == null) {
                logger.warn("No family found with ID: {}", familyId);
                return null;
            }
            List<Child> children = childRepository.findByFamilyId(familyId);
            logger.info("Retrieved {} children for family ID: {}", children.size(), familyId);
            return children;
        } catch (Exception e) {
            logger.error("Error retrieving children for family ID {}: {}", familyId, e.getMessage());
        }
        return null;
    }

    @Override
    public Optional<Optional<Child>> getChildById(String id) {
        try {
            Optional<Child> child = childRepository.findById(id);
            if (child == null) {
                logger.warn("No family found with ID: {}", id);
                return Optional.empty();
            }
            logger.info("Retrieved family: {} with ID: {}", child, id);
            return Optional.of(child);
        } catch (Exception e) {
            logger.error("Error retrieving child with ID {}: {}", id, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Child> getChildByLastName(String last_name) {
        return Optional.empty();
    }

    @Override
    public Child createChild(Child child) {
        return null;
    }
}
