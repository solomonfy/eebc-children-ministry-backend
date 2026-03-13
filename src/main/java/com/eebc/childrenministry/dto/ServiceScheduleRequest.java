package com.eebc.childrenministry.dto;

public record ServiceScheduleRequest(
        String serviceId,
        String classroomId,
        String roomId,
        String teacherId,
        String role,
        String status
) {}
