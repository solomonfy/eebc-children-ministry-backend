package com.eebc.childrenministry.dto;

public record TeacherSummaryDTO(
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
