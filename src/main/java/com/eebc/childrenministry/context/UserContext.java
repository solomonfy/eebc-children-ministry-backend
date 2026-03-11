package com.eebc.childrenministry.context;

import com.eebc.childrenministry.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class UserContext {

    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;

    // Returns the userId of whoever made the current HTTP request.
    // Returns null for unauthenticated requests (e.g. kiosk, public endpoints).
    public String getCurrentUserId() {
        try {
            String token = extractToken();
            if (token == null) return null;
            return jwtUtil.extractUserId(token);
        } catch (Exception e) {
            return null;
        }
    }

    public String getCurrentUserEmail() {
        try {
            String token = extractToken();
            if (token == null) return null;
            return jwtUtil.extractEmail(token);
        } catch (Exception e) {
            return null;
        }
    }

    public String getCurrentUserRole() {
        try {
            String token = extractToken();
            if (token == null) return null;
            return jwtUtil.extractRole(token);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractToken() {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7);
        return jwtUtil.isTokenValid(token) ? token : null;
    }
}