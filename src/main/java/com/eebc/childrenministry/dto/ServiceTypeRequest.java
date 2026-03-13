package com.eebc.childrenministry.dto;

public record ServiceTypeRequest(
        String ministryId,
        String code,
        String label,
        String color
) {}
