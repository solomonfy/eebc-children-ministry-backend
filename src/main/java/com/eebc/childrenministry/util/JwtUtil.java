package com.eebc.childrenministry.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:your-super-secret-key-change-this-in-production}")
    private String secret;

    @Value("${jwt.expiry:86400000}")
    private long expiryMs;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ── Token generation ───────────────────────
    // userId is now embedded as a claim so audit history
    // and other services can identify who made a change.
    public String generateToken(String email, String role, String userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)        // ← NEW
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Backwards-compatible overload ─────────
    // Remove once AuthController is updated to pass userId.
    @Deprecated
    public String generateToken(String email, String role) {
        return generateToken(email, role, null);
    }

    // ── Claim extraction ───────────────────────
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    public String extractFirstName(String token) {
        return extractAllClaims(token).get("firstName", String.class);
    }

    public String extractLastName(String token) {
        return extractAllClaims(token).get("lastName", String.class);
    }

    // ── Validation ─────────────────────────────
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).get("userName", String.class);
    }
}