package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.ChildAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChildAllergyRepository extends JpaRepository<ChildAllergy, String> {
}
