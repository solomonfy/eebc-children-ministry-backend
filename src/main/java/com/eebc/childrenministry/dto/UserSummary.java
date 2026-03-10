package com.eebc.childrenministry.dto;

public record UserSummary(
        String id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role,
        String photoUrl
) {
}
