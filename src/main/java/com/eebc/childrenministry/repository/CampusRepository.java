package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Campus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CampusRepository extends JpaRepository<Campus, UUID> {
    Optional<Campus> findById(String id);
}
