package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.ChildAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildAllergyRepository extends JpaRepository<ChildAllergy, String> {

    List<ChildAllergy> findByChild_Id(String childId);
}
