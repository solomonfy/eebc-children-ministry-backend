package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Attendance;
import com.eebc.childrenministry.repository.AttendanceRepository;
import com.eebc.childrenministry.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceRepository repository;
    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<Attendance>> list() {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Attendance>> listRoomAttendance(@RequestBody String roomId) {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @PostMapping
    public ResponseEntity<List<Attendance>> createMany(@RequestBody List<Attendance> records) {
        List<Attendance> saved = repository.saveAll(records);
        return ResponseEntity.ok(saved);
    }
}
