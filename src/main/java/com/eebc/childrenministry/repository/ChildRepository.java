package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, String> {
    Optional<Child> findById(String id);
    List<Child> findByFamilyId(String familyId);
}
