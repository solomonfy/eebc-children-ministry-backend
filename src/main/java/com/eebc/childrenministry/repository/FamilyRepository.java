package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FamilyRepository extends JpaRepository<Family, String> {
    Optional<Family> findByLastName(String lastName);
    Optional<Family> findById(String id);
}
