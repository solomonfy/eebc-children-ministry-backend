package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.RoomAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomAssignmentRepository extends JpaRepository<RoomAssignment, String> {
}
