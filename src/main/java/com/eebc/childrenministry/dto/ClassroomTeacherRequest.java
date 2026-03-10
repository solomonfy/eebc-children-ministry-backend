package com.eebc.childrenministry.dto;

// ── Assign a teacher to a classroom ──────────
public record ClassroomTeacherRequest(
        String userId,          // users.id with role TEACHER
        String defaultRoomId,   // optional — room within this classroom
        Boolean isLead          // true = lead teacher
) {
}
