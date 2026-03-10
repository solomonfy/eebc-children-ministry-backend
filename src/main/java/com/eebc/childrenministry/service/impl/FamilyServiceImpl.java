package com.eebc.childrenministry.service.impl;

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

    private static final Logger logger = LoggerFactory.getLogger(FamilyServiceImpl.class);

    @Override
    public List<Family> getAllFamilies() {
        List<Family> familyList = new ArrayList<>();
        try {
            familyList = familyRepository.findAll();
            logger.info("Retrieved {} families from the repository.", familyList);
            return familyList;
        } catch (Exception e) {
            logger.error("Error retrieving churches from the repository: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Optional<Family>> getFamilyById(String id) {
        try {
            Optional<Family> family = familyRepository.findById(id);
            if (family == null) {
                logger.warn("No family found with ID: {}", id);
                return Optional.empty();
            }
            logger.info("Retrieved family: {} with ID: {}", family, id);
            return Optional.of(family);
        } catch (Exception e) {
            logger.error("Error retrieving family with ID {}: {}", id, e.getMessage());
        }
        return Optional.empty();
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
        return null;
    }
}
