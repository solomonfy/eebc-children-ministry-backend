package com.eebc.childrenministry.dto;

public record ChildAllergyRequest(
        String allergyName,
        String severity,
        String reaction,
        String treatment,
        String notes
) {}
