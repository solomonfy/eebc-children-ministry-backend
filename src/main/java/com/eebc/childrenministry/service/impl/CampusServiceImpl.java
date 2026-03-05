package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.entity.Campus;
import com.eebc.childrenministry.repository.CampusRepository;
import com.eebc.childrenministry.service.CampusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CampusServiceImpl implements CampusService {

    private static final Logger logger = LoggerFactory.getLogger(ChurchServiceImpl.class);

    @Autowired
    CampusRepository campusRepository;

    @Override
    public List<Campus> getAllCampuses() {
        List<Campus> campusList = new ArrayList<>();
        try {
            campusList = campusRepository.findAll();
        } catch (Exception e) {
            logger.error("Error retrieving campuses from the repository: {}", e.getMessage());
            return Collections.emptyList();
        }
        logger.info("Retrieved {} campuses from the repository.", campusList);
        return campusList;
    }

    @Override
    public Optional<Campus> getCampusById(String id) {
        try {
            Optional<Campus> campus = campusRepository.findById(id);
            if (campus == null) {
                logger.warn("No campus found with ID: {}", id);
                return null;
            }
            logger.info("Retrieved campus: {} with ID: {}", campus, id);
            return campus;
        } catch (Exception e) {
            logger.error("Error retrieving campus with ID {}: {}", id, e.getMessage());
            return null;
        }
    }
}
