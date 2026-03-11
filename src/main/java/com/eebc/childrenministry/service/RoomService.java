package com.eebc.childrenministry.service;

import com.eebc.childrenministry.dto.RoomRequest;
import com.eebc.childrenministry.entity.Room;

import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();
    Room getRoomById(String id);
    Room createRoom(RoomRequest roomRequest);
    Room updateRoom(String id, RoomRequest roomRequest);
    void deleteRoom(String id);
    List<Room> getRoomsByClassroomId(String classroomId);
}
