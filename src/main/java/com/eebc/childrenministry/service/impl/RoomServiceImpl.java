package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.dto.RoomRequest;
import com.eebc.childrenministry.entity.Room;
import com.eebc.childrenministry.repository.RoomRepository;
import com.eebc.childrenministry.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomRepository roomRepository;

    @Override
    public List<Room> getAllRooms() {
        try {
            List<Room> rooms = roomRepository.findAll();
            logger.info("Fetched all rooms successfully, count: {}", rooms.size());
            return rooms;
        } catch (Exception e) {
            logger.error("Error fetching all rooms: {}", e.getMessage());
            throw e;
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
            throw e;
        }
    }

    @Override
    public Room createRoom(RoomRequest req) {
        try {
            if (req.name() == null || req.name().isBlank())
                throw new IllegalArgumentException("Room name is required");
            if (req.campusId() == null || req.campusId().isBlank())
                throw new IllegalArgumentException("Campus ID is required");
            if (req.ministryId() == null || req.ministryId().isBlank())
                throw new IllegalArgumentException("Ministry ID is required");

            Room room = new Room();
            room.setName(req.name());
            room.setCampusId(req.campusId());                                               // ← was campus_id
            room.setMinistryId(req.ministryId());                                           // ← was @ManyToOne, now plain String
            room.setClassroomId(req.classroomId());                                         // nullable — ok
            room.setCapacity(req.capacity() != null ? req.capacity() : 20);
            room.setLocation(req.location());
            room.setMinAgeMonths(req.minAgeMonths() != null ? req.minAgeMonths() : 0);
            room.setMaxAgeMonths(req.maxAgeMonths() != null ? req.maxAgeMonths() : 216);
            room.setRatioChildToVolunteer(req.ratioChildToVolunteer() != null ? req.ratioChildToVolunteer() : 5);
            room.setStatus(req.status() != null ? req.status() : "ACTIVE");

            Room saved = roomRepository.save(room);
            logger.info("Created new room: id={}, name={}", saved.getId(), saved.getName());
            return saved;
        } catch (Exception e) {
            logger.error("Error creating new room: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Room updateRoom(String id, RoomRequest req) {
        try {
            Room room = roomRepository.findById(id).orElse(null);
            if (room == null) {
                logger.warn("Room with id {} not found for update", id);
                return null;
            }

            if (req.name()       != null) room.setName(req.name());
            if (req.campusId()   != null) room.setCampusId(req.campusId());
            if (req.ministryId() != null) room.setMinistryId(req.ministryId());
            if (req.classroomId()!= null) room.setClassroomId(req.classroomId());
            if (req.capacity()   != null) room.setCapacity(req.capacity());
            if (req.location()   != null) room.setLocation(req.location());
            if (req.minAgeMonths()          != null) room.setMinAgeMonths(req.minAgeMonths());
            if (req.maxAgeMonths()          != null) room.setMaxAgeMonths(req.maxAgeMonths());
            if (req.ratioChildToVolunteer() != null) room.setRatioChildToVolunteer(req.ratioChildToVolunteer());
            if (req.status()     != null) room.setStatus(req.status());

            Room updated = roomRepository.save(room);
            logger.info("Updated room with id {} successfully", id);
            return updated;
        } catch (Exception e) {
            logger.error("Error updating room with id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteRoom(String id) {
        try {
            if (roomRepository.existsById(id)) {
                roomRepository.deleteById(id);
                logger.info("Deleted room with id {} successfully", id);
            } else {
                logger.warn("Room with id {} not found for deletion", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting room with id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Room> getRoomsByClassroomId(String classroomId) {
        try {
            List<Room> rooms = roomRepository.findByClassroomId(classroomId);
            logger.info("Fetched rooms by classroomId {}, count: {}", classroomId, rooms.size());
            return rooms;
        } catch (Exception e) {
            logger.error("Error fetching rooms by classroomId {}: {}", classroomId, e.getMessage());
            throw e;
        }
    }
}