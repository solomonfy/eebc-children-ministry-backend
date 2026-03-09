package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.ClassroomRequest;
import com.eebc.childrenministry.dto.ClassroomTeacherRequest;
import com.eebc.childrenministry.dto.ClassroomDetailResponse;
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

    @Autowired
    private ClassroomService classroomService;

    // ── Classroom CRUD ────────────────────────

    // GET /classrooms          → all classrooms (basic list)
    // GET /classrooms?active=true → active only
    @GetMapping
    public ResponseEntity<List<Classroom>> list(
            @RequestParam(required = false, defaultValue = "false") boolean active) {
        List<Classroom> result = active
                ? classroomService.getActive()
                : classroomService.getAll();
        return ResponseEntity.ok(result);
    }

    // GET /classrooms/{id}     → basic classroom record
    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getById(@PathVariable String id) {
        return ResponseEntity.ok(classroomService.getById(id));
    }

    // GET /classrooms/{id}/detail
    // Returns classroom + coordinator + assistant + teachers + rooms in one call
    @GetMapping("/{id}/detail")
    public ResponseEntity<ClassroomDetailResponse> getDetail(@PathVariable String id) {
        return ResponseEntity.ok(classroomService.getDetail(id));
    }

    // POST /classrooms
    @PostMapping
    public ResponseEntity<Classroom> create(@RequestBody ClassroomRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(classroomService.create(req));
    }

    // PUT /classrooms/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Classroom> update(
            @PathVariable String id,
            @RequestBody ClassroomRequest req) {
        return ResponseEntity.ok(classroomService.update(id, req));
    }

    // DELETE /classrooms/{id}  → soft delete (sets status=INACTIVE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        classroomService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Classroom deactivated"));
    }

    // ── Teacher Assignments ───────────────────

    // GET /classrooms/{id}/teachers
    @GetMapping("/{id}/teachers")
    public ResponseEntity<List<ClassroomTeacher>> getTeachers(@PathVariable String id) {
        return ResponseEntity.ok(classroomService.getTeachers(id));
    }

    // POST /classrooms/{id}/teachers   → assign a teacher
    @PostMapping("/{id}/teachers")
    public ResponseEntity<ClassroomTeacher> assignTeacher(
            @PathVariable String id,
            @RequestBody ClassroomTeacherRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(classroomService.assignTeacher(id, req));
    }

    // PUT /classrooms/assignments/{assignmentId}  → update default room or lead status
    @PutMapping("/assignments/{assignmentId}")
    public ResponseEntity<ClassroomTeacher> updateAssignment(
            @PathVariable String assignmentId,
            @RequestBody ClassroomTeacherRequest req) {
        return ResponseEntity.ok(classroomService.updateTeacherAssignment(assignmentId, req));
    }

    // DELETE /classrooms/assignments/{assignmentId}  → remove teacher from classroom
    @DeleteMapping("/assignments/{assignmentId}")
    public ResponseEntity<Map<String, String>> removeTeacher(@PathVariable String assignmentId) {
        classroomService.removeTeacher(assignmentId);
        return ResponseEntity.ok(Map.of("message", "Teacher removed from classroom"));
    }

    // ── Utility ───────────────────────────────

    // GET /classrooms/suggest?ageMonths=18
    // Returns the best matching classroom for a child's age
    @GetMapping("/suggest")
    public ResponseEntity<?> suggest(@RequestParam int ageMonths) {
        Classroom match = classroomService.suggestClassroom(ageMonths);
        if (match == null) {
            return ResponseEntity.ok(Map.of("message", "No matching classroom found"));
        }
        return ResponseEntity.ok(match);
    }
}
