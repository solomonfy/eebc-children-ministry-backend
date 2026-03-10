package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.entity.Volunteer;
import com.eebc.childrenministry.repository.VolunteerRepository;
import com.eebc.childrenministry.service.VolunteerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    private static final Logger logger = LoggerFactory.getLogger(VolunteerServiceImpl.class);

    @Autowired
    VolunteerRepository volunteerRepository;

//    private final VolunteerRepository volunteerRepository;
//
//    public VolunteerServiceImpl(VolunteerRepository volunteerRepository) {
//        this.volunteerRepository = volunteerRepository;
//    }

    @Override
    public List<Volunteer> getAllVolunteers() {
        List<Volunteer> volunteerList = new ArrayList<>();
        try {
            volunteerList = volunteerRepository.findAll();
            logger.info("Retrieved {} volunteers from the repository.", volunteerList);
            return volunteerList;
        } catch (Exception e) {
            logger.error("Error retrieving volunteers from the repository: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Volunteer> getVolunteerById(String id) {
        try {
            Optional<Volunteer> volunteer = volunteerRepository.findById(id);
            if (volunteer == null) {
                logger.warn("No volunteer found with ID: {}", id);
                return Optional.empty();
            }
            logger.info("Retrieved volunteer: {} with ID: {}", volunteer, id);
        } catch (Exception e) {
            logger.error("Error retrieving volunteer with ID {}: {}", id, e.getMessage());
        }
        return null;
    }

    @Override
    public Volunteer createVolunteer(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);
    }

    @Override
    public Volunteer updateVolunteer(String id, Volunteer updatedVolunteer) {
        return volunteerRepository.findById(id).map(volunteer -> {
            volunteer.setLastName(updatedVolunteer.getLastName());
            volunteer.setEmail(updatedVolunteer.getEmail());
            volunteer.setPhone(updatedVolunteer.getPhone());
            return volunteerRepository.save(volunteer);
        }).orElseThrow(() -> new RuntimeException("Volunteer not found with id: " + id));
    }

    @Override
    public void deleteVolunteer(String id) {
        volunteerRepository.deleteById(id);
    }

//    @Override
//    public Optional<Volunteer> getVolunteerByLastName(String lastName) {
//        try {
//            List<Volunteer> volunteers = volunteerRepository.findAll();
//            for (Volunteer volunteer : volunteers) {
//                if (volunteer.getLastName().equalsIgnoreCase(lastName)) {
//                    logger.info("Retrieved volunteer: {} with last name: {}", volunteer, lastName);
//                    return Optional.of(volunteer);
//                }
//            }
//            logger.warn("No volunteer found with last name: {}", lastName);
//        } catch (Exception e) {
//            logger.error("Error retrieving volunteer with last name {}: {}", lastName, e.getMessage());
//        }
//        return Optional.empty();
//    }
}
