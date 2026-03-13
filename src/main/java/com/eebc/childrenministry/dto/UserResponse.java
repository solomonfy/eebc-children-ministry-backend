package com.eebc.childrenministry.dto;

// Safe user response — password_hash is NEVER included.
// Used by all endpoints that return user data to the frontend.
public record UserResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String userName,
        String phone,
        String photoUrl,
        String role,
        String status,
        Boolean notifyEmail,
        Boolean notifySms
) {}