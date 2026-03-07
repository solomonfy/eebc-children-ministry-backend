package com.eebc.childrenministry.service;

import com.eebc.childrenministry.entity.Family;

import java.util.List;
import java.util.Optional;

public interface FamilyService {
    List<Family> getAllFamilies();
    Optional<Optional<Family>> getFamilyById(String id);
    Optional<Family> getFamilyByName(String name);
    Optional<Family> getFamilyByPin(String pin);
    Family createFamily(Family family);
}
