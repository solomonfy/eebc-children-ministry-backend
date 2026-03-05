package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    Attendance findByChildIdAndServiceId(String childId, String serviceId);
}
