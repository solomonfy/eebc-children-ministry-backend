package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Room;
import com.eebc.childrenministry.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;

    @GetMapping
    public ResponseEntity<List<Room>> list() {
        return ResponseEntity.ok(roomRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Room> create(@RequestBody Room r) {
        Room saved = roomRepository.save(r);
        return ResponseEntity.ok(saved);
    }
}
