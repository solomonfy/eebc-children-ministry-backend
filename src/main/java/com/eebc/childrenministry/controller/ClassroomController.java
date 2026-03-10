package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.*;
import com.eebc.childrenministry.entity.Classroom;
import com.eebc.childrenministry.entity.ClassroomTeacher;
import com.eebc.childrenministry.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/classrooms")
public class ClassroomController {

    // Spring auto-wires ClassroomServiceImpl since it implements ClassroomService
    @Autowired
    private ClassroomService classroomService;

    // ── Classroom CRUD ────────────────────────

    @GetMapping
    public ResponseEntity<List<Classroom>> list(
            @RequestParam(required = false, defaultValue = "false") boolean active) {
        return ResponseEntity.ok(
                active ? classroomService.getActive() : classroomService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getById(@PathVariable String id) {
        return ResponseEntity.ok(classroomService.getById(id));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ClassroomDetailResponse> getDetail(@PathVariable String id) {
        return ResponseEntity.ok(classroomService.getDetail(id));
    }

    @PostMapping
    public ResponseEntity<Classroom> create(@RequestBody ClassroomRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classroom> update(
            @PathVariable String id, @RequestBody ClassroomRequest req) {
        return ResponseEntity.ok(classroomService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        classroomService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Classroom deactivated"));
    }

    // ── Teacher Assignments ───────────────────

    // Returns TeacherSummaryDTO — includes firstName, lastName, photoUrl
    // joined from the users table inside ClassroomServiceImpl.getTeachers()
    @GetMapping("/{id}/teachers")
    public ResponseEntity<List<TeacherSummaryDTO>> getTeachers(@PathVariable String id) {
        return ResponseEntity.ok(classroomService.getTeachers(id));
    }

    @PostMapping("/{id}/teachers")
    public ResponseEntity<ClassroomTeacher> assignTeacher(
            @PathVariable String id, @RequestBody ClassroomTeacherRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(classroomService.assignTeacher(id, req));
    }

    @PutMapping("/assignments/{assignmentId}")
    public ResponseEntity<ClassroomTeacher> updateAssignment(
            @PathVariable String assignmentId, @RequestBody ClassroomTeacherRequest req) {
        return ResponseEntity.ok(classroomService.updateTeacherAssignment(assignmentId, req));
    }

    @DeleteMapping("/assignments/{assignmentId}")
    public ResponseEntity<Map<String, String>> removeTeacher(@PathVariable String assignmentId) {
        classroomService.removeTeacher(assignmentId);
        return ResponseEntity.ok(Map.of("message", "Teacher removed from classroom"));
    }

    // ── Utility ───────────────────────────────

    @GetMapping("/suggest")
    public ResponseEntity<?> suggest(@RequestParam int ageMonths) {
        Classroom match = classroomService.suggestClassroom(ageMonths);
        return match == null
                ? ResponseEntity.ok(Map.of("message", "No matching classroom found"))
                : ResponseEntity.ok(match);
    }
}