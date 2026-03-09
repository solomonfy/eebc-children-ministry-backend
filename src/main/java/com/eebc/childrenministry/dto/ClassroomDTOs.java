package com.eebc.childrenministry.dto;

import java.util.List;

// ── Create / Update classroom ─────────────────
public record ClassroomRequest(
        String name,
        String description,
        String ageGroup,
        Integer minAgeMonths,
        Integer maxAgeMonths,
        String coordinatorId,   // users.id with role CLASS_COORDINATOR
        String assistantId,     // users.id with role CLASS_ASSISTANT
        String campusId,
        Integer maxCapacity,
        String status
) {}


// ── Assign a teacher to a classroom ──────────
public record ClassroomTeacherRequest(
        String userId,          // users.id with role TEACHER
        String defaultRoomId,   // optional — room within this classroom
        Boolean isLead          // true = lead teacher
) {}


// ── Rich response for classroom detail view ───
// Returned by GET /classrooms/{id}
// Includes coordinator, assistant, teachers, and rooms
// assembled by ClassroomService so frontend gets
// everything in one call
public record ClassroomDetailResponse(
        String id,
        String name,
        String description,
        String ageGroup,
        Integer minAgeMonths,
        Integer maxAgeMonths,
        String status,
        Integer maxCapacity,

        // Coordinator info
        UserSummary coordinator,

        // Assistant info
        UserSummary assistant,

        // Teachers assigned to this classroom
        List<TeacherSummary> teachers,

        // Rooms that belong to this classroom
        List<RoomSummary> rooms,

        // Computed stats
        int teacherCount,
        int roomCount,
        int totalCapacity
) {}


// ── Nested summaries ──────────────────────────

public record UserSummary(
        String id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role,
        String photoUrl
) {}

public record TeacherSummary(
        String assignmentId,
        String userId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String photoUrl,
        boolean isLead,
        String defaultRoomId,
        String defaultRoomName,
        String status
) {}

public record RoomSummary(
        String id,
        String name,
        String capacity,
        Long minAgeMonths,
        Long maxAgeMonths,
        Long ratioChildToVolunteer,
        String status,
        // Teachers whose defaultRoom is this room
        List<String> teacherNames
) {}
