package com.eebc.childrenministry.service;

import com.eebc.childrenministry.dto.*;
import com.eebc.childrenministry.entity.Classroom;
import com.eebc.childrenministry.entity.ClassroomTeacher;

import java.util.List;

public interface ClassroomService {

    // ── Classroom CRUD ────────────────────────
    Classroom          create(ClassroomRequest req);
    List<Classroom>    getAll();
    List<Classroom>    getActive();
    Classroom          getById(String id);
    Classroom          update(String id, ClassroomRequest req);
    void               delete(String id);

    // ── Rich detail (classroom + coordinator + assistant + teachers + rooms) ──
    ClassroomDetailResponse getDetail(String id);

    // ── Teacher assignments ───────────────────
    ClassroomTeacher        assignTeacher(String classroomId, ClassroomTeacherRequest req);
    ClassroomTeacher        updateTeacherAssignment(String assignmentId, ClassroomTeacherRequest req);
    void                    removeTeacher(String assignmentId);

    // Returns enriched list — firstName, lastName, photoUrl joined from users table
    // so the UI can render avatars without a second API call
    List<TeacherSummaryDTO> getTeachers(String classroomId);

    // ── Auto-suggest classroom by child age ───
    Classroom suggestClassroom(int ageMonths);
}