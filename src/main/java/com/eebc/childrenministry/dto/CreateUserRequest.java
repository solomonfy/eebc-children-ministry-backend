package com.eebc.childrenministry.dto;

// Request DTO for POST /users
// Uses "password" (plain text) instead of "passwordHash"
// so the frontend never has to know about internal field names.
public record CreateUserRequest(
        String firstName,
        String lastName,
        String email,
        String userName,
        String password,        // ← plain text, hashed in service before persisting
        String phone,
        String photoUrl,
        String role,
        String status
) {}