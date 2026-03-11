package com.eebc.childrenministry.config;

// ─────────────────────────────────────────────────────────────
// ADD these lines to your existing JwtAuthFilter.doFilterInternal()
// right after the JWT is validated and the username is extracted.
//
// Find the block where you do:
//   String username = jwtUtil.extractUsername(jwt);
//   UsernamePasswordAuthenticationToken authToken = ...
//   SecurityContextHolder.getContext().setAuthentication(authToken);
//
// Then add BELOW it:
// ─────────────────────────────────────────────────────────────

/*

    // ── Populate RequestContext for audit trail ──
    try {
        RequestContext ctx = applicationContext.getBean(RequestContext.class);

        // userId — try to get from token claims, fallback to username
        String userId = jwtUtil.extractClaim(jwt, claims -> claims.get("userId", String.class));
        ctx.setUserId(userId != null ? userId : username);

        // display name — try firstName + lastName from claims
        String firstName = jwtUtil.extractClaim(jwt, claims -> claims.get("firstName", String.class));
        String lastName  = jwtUtil.extractClaim(jwt, claims -> claims.get("lastName", String.class));
        if (firstName != null) {
            ctx.setUserName(firstName + (lastName != null ? " " + lastName : ""));
        } else {
            ctx.setUserName(username);
        }

        // IP address — handle proxies
        String ip = request.getHeader("X-Forwarded-For");
        ctx.setIpAddress(ip != null ? ip.split(",")[0].trim() : request.getRemoteAddr());

        // User agent
        ctx.setUserAgent(request.getHeader("User-Agent"));

    } catch (Exception e) {
        // Never let audit context failure break authentication
        logger.warn("Could not populate RequestContext: {}", e.getMessage());
    }

*/

// ─────────────────────────────────────────────────────────────
// Also inject ApplicationContext into your JwtAuthFilter:
//
//   @Autowired
//   private ApplicationContext applicationContext;
//
// This lets us fetch the @RequestScope RequestContext bean safely.
// ─────────────────────────────────────────────────────────────
public class JwtAuthFilterAuditPatch {
    // This file is instructions only — apply the patch above to your JwtAuthFilter.java
}
