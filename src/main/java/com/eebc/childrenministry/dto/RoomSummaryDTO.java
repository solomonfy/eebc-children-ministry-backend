package com.eebc.childrenministry.dto;

import java.util.List;

public record RoomSummaryDTO(
        String id,
        String name,
        Integer capacity,
        Integer minAgeMonths,
        Integer maxAgeMonths,
        Integer ratioChildToVolunteer,
        String status,
        // Teachers whose defaultRoom is this room
        List<String> teacherNames
) {
}
