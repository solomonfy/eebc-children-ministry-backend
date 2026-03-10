package com.eebc.childrenministry.service;

import com.eebc.childrenministry.entity.Room;

import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();
    Room getRoomById(String id);
    Room createRoom(Room room);
    Room updateRoom(String id, Room room);
    void deleteRoom(String id);
    List<Room> getRoomsByClassroomId(String classroomId);
}
