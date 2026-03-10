package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.entity.Room;
import com.eebc.childrenministry.repository.RoomRepository;
import com.eebc.childrenministry.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    @Autowired
    RoomRepository roomRepository;

    @Override
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try {
            rooms = roomRepository.findAll();
            logger.info("Fetched all rooms successfully, count: {}", rooms.size());
            return rooms;
        } catch (Exception e) {
            logger.error("Error fetching all rooms: {}", e.getMessage());
            throw e; // Rethrow the exception to be handled by the controller
        }
    }

    @Override
    public Room getRoomById(String id) {
        try {
            Room room = roomRepository.findById(id).orElse(null);
            logger.info("Fetched room by id {}: {}", id, room != null ? "Found" : "Not Found");
            return room;
        } catch (Exception e) {
            logger.error("Error fetching room by id {}: {}", id, e.getMessage());
            throw e; // Rethrow the exception to be handled by the controller
        }
    }

    @Override
    public Room createRoom(Room room) {
        return null;
    }

    @Override
    public Room updateRoom(String id, Room room) {
        return null;
    }

    @Override
    public void deleteRoom(String id) {

    }

    @Override
    public List<Room> getRoomsByClassroomId(String classroomId) {
        return null;
    }
}
