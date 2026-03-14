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
    public String generateToken(String email, String role, String userId,
                                String firstName, String lastName, String userName,
                                String campusId, String churchId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)
                .claim("firstName", firstName)
                .claim("lastName", lastName)
                .claim("userName", userName)
                .claim("campusId", campusId)
                .claim("churchId", churchId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Backwards-compatible overloads ────────
    @Deprecated
    public String generateToken(String email, String role, String userId,
                                String firstName, String lastName, String userName) {
        return generateToken(email, role, userId, firstName, lastName, userName, null, null);
    }

    @Deprecated
    public String generateToken(String email, String role, String userId) {
        return generateToken(email, role, userId, null, null, null, null, null);
    }

    @Deprecated
    public String generateToken(String email, String role) {
        return generateToken(email, role, null, null, null, null, null, null);
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

    public String extractCampusId(String token) {
        return extractAllClaims(token).get("campusId", String.class);
    }

    public String extractChurchId(String token) {
        return extractAllClaims(token).get("churchId", String.class);
    }
}