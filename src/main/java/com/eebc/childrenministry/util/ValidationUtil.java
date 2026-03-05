package com.eebc.childrenministry.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
@Component
public class ValidationUtil {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return "Email cannot be empty";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Invalid email format";
        }
        return null;
    }

    public String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must include a lowercase letter";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must include an uppercase letter";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Password must include a digit";
        }
        return null;
    }

    public String validateRole(String role) {
        if (role == null || role.isBlank()) {
            return "Role cannot be empty";
        }
        if (!role.matches("admin|staff|volunteer")) {
            return "Role must be one of: admin, staff, volunteer";
        }
        return null;
    }

    public String validateUserName(String user_name) {
        if (user_name == null || user_name.isBlank()) {
            return "Name cannot be empty";
        }
        if (!user_name.matches("^[A-Za-z ]+$")) {
            return "Name must contain only letters and spaces";
        }
        return null;
    }
}