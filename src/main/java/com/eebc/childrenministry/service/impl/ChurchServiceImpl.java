package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.entity.Church;
import com.eebc.childrenministry.repository.ChurchRepository;
import com.eebc.childrenministry.service.ChurchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChurchServiceImpl implements ChurchService {

    private static final Logger logger = LoggerFactory.getLogger(ChurchServiceImpl.class);

    @Autowired
    private ChurchRepository churchRepository;

    @Override
    public List<Church> getAllChurches() {
        List<Church> allChurches = new ArrayList<>();
        try {
            allChurches = churchRepository.findAll();
            logger.info("Retrieved {} churches from the repository.", allChurches.size());
            return allChurches;
        } catch (Exception e) {
            // Log the exception (not implemented here)
            logger.error("Error retrieving churches from the repository: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Church> getChurchById(String id) {
        try {
            Optional<Church> church = churchRepository.findById(id);
            if (church.isPresent()) {
                logger.info("Retrieved church: {} with ID: {}", church.get(), id);
                return church;
            } else {
                logger.warn("No church found with ID: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error retrieving church with ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Church saveChurch(Church church) {
        try {
            logger.info("Saving church: {}", church);
            return churchRepository.save(church);
        } catch (Exception e) {
            logger.error("Error saving church: {}", e.getMessage());
            return null;
        }
    }

}
