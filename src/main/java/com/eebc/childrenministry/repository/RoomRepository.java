package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.dto.RoomRequest;
import com.eebc.childrenministry.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, String> {
    List<Room> findByClassroomId(String id);
}
