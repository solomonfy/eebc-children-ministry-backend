package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.ClassroomTeacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassroomTeacherRepository extends JpaRepository<ClassroomTeacher, String> {

    // All teacher assignments for a classroom
    List<ClassroomTeacher> findByClassroomId(String classroomId);

    // Active assignments only
    List<ClassroomTeacher> findByClassroomIdAndStatus(String classroomId, String status);

    // All classrooms a specific teacher is assigned to
    List<ClassroomTeacher> findByUserId(String userId);

    // Check if a teacher is already assigned to a classroom
    Optional<ClassroomTeacher> findByClassroomIdAndUserId(String classroomId, String userId);

    // All teachers whose default room is a specific room
    List<ClassroomTeacher> findByDefaultRoomId(String defaultRoomId);

    // Lead teachers only for a classroom
    List<ClassroomTeacher> findByClassroomIdAndIsLeadTrue(String classroomId);
}
