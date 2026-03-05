package com.eebc.childrenministry.service;

import com.eebc.childrenministry.entity.Family;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public interface FamilyService {
    List<Family> getAllFamilies();
    Optional<Optional<Family>> getFamilyById(String id);
    Optional<Family> getFamilyByName(String name);
    Family createFamily(Family family);

}
