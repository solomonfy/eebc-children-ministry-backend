package com.eebc.childrenministry.service;

import com.eebc.childrenministry.entity.Family;
import com.eebc.childrenministry.entity.Volunteer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface VolunteerService {
    List<Volunteer> getAllVolunteers();
    Optional<Volunteer> getVolunteerById(String id);
    Volunteer createVolunteer(Volunteer volunteer);
    Volunteer updateVolunteer(String id, Volunteer volunteer);
    void deleteVolunteer(String id);
}
