package com.eebc.childrenministry.dto;

public record RegisterRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String role
) {}