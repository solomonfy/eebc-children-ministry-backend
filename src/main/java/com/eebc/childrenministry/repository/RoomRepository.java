package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, String> {
    List<Room> findByClassroomId(String id);

    @Query("SELECT r FROM Room r WHERE r.status = 'ACTIVE' AND (r.minAgeMonths IS NULL OR r.minAgeMonths <= :ageMonths) AND (r.maxAgeMonths IS NULL OR r.maxAgeMonths >= :ageMonths)")
    List<Room> findByAgeInMonths(@Param("ageMonths") int ageMonths);
}
