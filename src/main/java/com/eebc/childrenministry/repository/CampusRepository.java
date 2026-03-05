package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Campus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampusRepository extends JpaRepository<Campus, String> {
    Optional<Campus> findById(String id);
}
