package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Ministry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MinistryRepository extends JpaRepository<Ministry, String> {
}
