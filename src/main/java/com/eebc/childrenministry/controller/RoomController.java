package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.RoomRequest;
import com.eebc.childrenministry.entity.Room;
import com.eebc.childrenministry.repository.RoomRepository;
import com.eebc.childrenministry.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;


    // GET /rooms
    @GetMapping
    public ResponseEntity<List<Room>> list() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    // GET /rooms/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Room> get(@PathVariable String id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    // POST /rooms
    @PostMapping
    public ResponseEntity<Room> create(@RequestBody RoomRequest req) {
        if (req.name() == null || req.name().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (req.ministryId() == null || req.ministryId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Room room = new Room();
        room.setName(req.name());
        room.setCapacity(req.capacity() != null ? req.capacity() : 20);
        room.setLocation(req.location());
        room.setMinAgeMonths(req.minAgeMonths() != null ? req.minAgeMonths() : 0);
        room.setMaxAgeMonths(req.maxAgeMonths() != null ? req.maxAgeMonths() : 216);
        room.setRatioChildToVolunteer(req.ratioChildToVolunteer() != null ? req.ratioChildToVolunteer() : 5);
        room.setCampus_id(req.campusId());
        room.setMinistry_id(req.ministryId());
        room.setClassroomId(req.classroomId());
        room.setStatus(req.status() != null ? req.status() : "ACTIVE");

        return ResponseEntity.ok(roomService.createRoom(room));
    }

    // PUT /rooms/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Room> update(@PathVariable String id, @RequestBody RoomRequest req) {
        Room room = roomService.getRoomById(id);
        if (req.name()       != null) room.setName(req.name());
        if (req.capacity()   != null) room.setCapacity(req.capacity());
        if (req.location()   != null) room.setLocation(req.location());
        if (req.minAgeMonths()           != null) room.setMinAgeMonths(req.minAgeMonths());
        if (req.maxAgeMonths()           != null) room.setMaxAgeMonths(req.maxAgeMonths());
        if (req.ratioChildToVolunteer()  != null) room.setRatioChildToVolunteer(req.ratioChildToVolunteer());
        if (req.ministryId() != null) room.setMinistry_id(req.ministryId());
        if (req.classroomId()!= null) room.setClassroomId(req.classroomId());
        if (req.status()     != null) room.setStatus(req.status());

        return ResponseEntity.ok(roomService.updateRoom(id, room));
    };

    // DELETE /rooms/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (roomService.getRoomById(id) != null) return ResponseEntity.notFound().build();
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
