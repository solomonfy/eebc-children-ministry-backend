package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GuardianRepository extends JpaRepository<Guardian, String> {
}
