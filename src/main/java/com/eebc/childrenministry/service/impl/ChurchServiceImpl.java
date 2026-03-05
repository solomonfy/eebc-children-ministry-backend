package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.entity.Church;
import com.eebc.childrenministry.repository.ChurchRepository;
import com.eebc.childrenministry.service.ChurchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ChurchServiceImpl implements ChurchService {

    private static final Logger logger = LoggerFactory.getLogger(ChurchServiceImpl.class);

    @Autowired
    private ChurchRepository churchRepository;

    @Override
    public List<Church> getAllChurches() {
        List<Church> churchList = new ArrayList<>();
        try {
            churchList = churchRepository.findAll();
        } catch (Exception e) {
            logger.error("Error retrieving churches from the repository: {}", e.getMessage());
            return Collections.emptyList();
        }
        logger.info("Retrieved {} churches from the repository.", churchList);
        return churchList;
    }

    @Override
    public Optional<Church> getChurchById(String id) {
        try {
            Optional<Church> church = churchRepository.findById(id);
            if (church == null) {
                logger.warn("No church found with ID: {}", id);
                return null;
            }
            logger.info("Retrieved church: {} with ID: {}", church, id);
            return church;
        } catch (Exception e) {
            logger.error("Error retrieving church with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

}
