package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Attendance;
import com.eebc.childrenministry.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<Attendance>> list() {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Attendance>> listRoomAttendance(@PathVariable String roomId) {
        List<Attendance> records = attendanceService.getAllAttendances()
                .stream()
                .filter(a -> roomId.equals(a.getRoomId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(records);
    }

    @PostMapping
    public ResponseEntity<List<Attendance>> createMany(@RequestBody List<Attendance> records) {
        List<Attendance> saved = records.stream()
                .map(attendanceService::createAttendance)
                .collect(Collectors.toList());
        return ResponseEntity.ok(saved);
    }
}
