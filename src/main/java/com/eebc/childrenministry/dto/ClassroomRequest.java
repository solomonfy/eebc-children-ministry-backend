package com.eebc.childrenministry.dto;

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
) {
}
