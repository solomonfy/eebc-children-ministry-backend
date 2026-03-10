package com.eebc.childrenministry.dto;


public record RoomRequest(
        String  name,
        Integer capacity,
        String  location,
        Integer minAgeMonths,
        Integer maxAgeMonths,
        Integer ratioChildToVolunteer,
        String  campusId,
        String  ministryId,
        String  classroomId,
        String  status
) {}
