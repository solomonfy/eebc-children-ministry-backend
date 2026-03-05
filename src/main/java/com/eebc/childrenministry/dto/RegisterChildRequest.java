package com.eebc.childrenministry.dto;

import java.time.LocalDate;

public record RegisterChildRequest (
    String familyId,
    String firstName,
    String lastName,
    String nickname,
    LocalDate birthDate,
    String gender,
    String grade,
    String specialNeeds,
    String notes,
    String pickupPin   // ← raw PIN from frontend, 4-6 digits
){}
