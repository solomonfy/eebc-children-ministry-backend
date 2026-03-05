package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VolunteerRepository extends JpaRepository<Volunteer, String> {
    Optional<Volunteer> findById(String id);
    void deleteById(String id);
}
