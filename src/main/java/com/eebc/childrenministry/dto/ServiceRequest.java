package com.eebc.childrenministry.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ServiceRequest(
        String name,
        LocalDate serviceDate,
        LocalTime startTime,
        LocalTime endTime,
        String type,
        String status,
        String campusId,
        String ministryId
) {}
