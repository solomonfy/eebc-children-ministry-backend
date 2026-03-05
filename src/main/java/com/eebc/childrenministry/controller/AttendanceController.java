package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.entity.Attendance;
import com.eebc.childrenministry.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceRepository repository;

    @PostMapping
    public ResponseEntity<List<Attendance>> createMany(@RequestBody List<Attendance> records) {
        List<Attendance> saved = repository.saveAll(records);
        return ResponseEntity.ok(saved);
    }
}
