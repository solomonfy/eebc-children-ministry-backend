package com.eebc.childrenministry.service;

import com.eebc.childrenministry.entity.Campus;

import java.util.List;
import java.util.Optional;

public interface CampusService {
    List<Campus> getAllCampuses();
    Optional<Campus> getCampusById(String id);
}
