package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, String> {
    Optional<Family> findByLastName(String lastName);
    Optional<Family> findById(String id);
    List<Family> findAllByCampusId(String campusId);
    Optional<Family> findByIdAndCampusId(String id, String campusId);
}
