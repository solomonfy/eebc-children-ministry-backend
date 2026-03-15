package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.RoomRequest;
import com.eebc.childrenministry.entity.Child;
import com.eebc.childrenministry.entity.Room;
import com.eebc.childrenministry.repository.ChildRepository;
import com.eebc.childrenministry.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final ChildRepository childRepository;

    // GET /rooms
    @GetMapping
    public ResponseEntity<List<Room>> list() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    // GET /rooms/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Room> get(@PathVariable String id) {
        Room room = roomService.getRoomById(id);
        return room != null
                ? ResponseEntity.ok(room)
                : ResponseEntity.notFound().build();
    }

    // POST /rooms
    @PostMapping
    public ResponseEntity<Room> create(@RequestBody RoomRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.createRoom(req));
    }

    // PUT /rooms/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Room> update(@PathVariable String id, @RequestBody RoomRequest req) {
        Room updated = roomService.updateRoom(id, req);
        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    // DELETE /rooms/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(Map.of("message", "Room deleted"));
    }

    // GET /rooms/{id}/children — children whose defaultRoomId = this room
    @GetMapping("/{id}/children")
    public ResponseEntity<List<Child>> getChildren(@PathVariable String id) {
        return ResponseEntity.ok(childRepository.findByDefaultRoomId(id));
    }
}