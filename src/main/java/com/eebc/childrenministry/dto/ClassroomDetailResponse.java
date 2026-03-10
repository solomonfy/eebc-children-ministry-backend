package com.eebc.childrenministry.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
        List<TeacherSummaryDTO> teachers,

        // Rooms that belong to this classroom
        List<RoomSummaryDTO> rooms,

        // Computed stats
        int teacherCount,
        int roomCount,
        int totalCapacity
) {
}
