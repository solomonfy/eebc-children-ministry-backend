package com.eebc.childrenministry.config;

import com.eebc.childrenministry.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import org.apache.catalina.core.ApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

   @Autowired
   private ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
//        System.out.println(">>> Auth header: " + authHeader);
//        System.out.println(">>> Request URI: " + request.getRequestURI());

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtil.isTokenValid(token)) {
                    String email = jwtUtil.extractEmail(token);
                    String role = jwtUtil.extractRole(token);
//                    System.out.println(">>> Valid token for: " + email + " role: " + role);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email, null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // ── Populate RequestContext for audit trail ──
                    try {
                        String username = jwtUtil.extractUsername(token);
                        RequestContext ctx = applicationContext.getBean(RequestContext.class);

                        // userId — try to get from token claims, fallback to username

                        String userId = jwtUtil.extractUserId(token);
                        ctx.setUserId(userId != null ? userId : username);

                        // display name — try firstName + lastName from claims
                        String firstName = jwtUtil.extractFirstName(token);
                        String lastName  = jwtUtil.extractLastName(token);
                        if (firstName != null) {
                            ctx.setUserName(firstName + (lastName != null ? " " + lastName : ""));
                        } else {
                            ctx.setUserName(username);
                        }

                        // Campus / church / role for data scoping
                        ctx.setCampusId(jwtUtil.extractCampusId(token));
                        ctx.setChurchId(jwtUtil.extractChurchId(token));
                        ctx.setRole(role);

                        // IP address — handle proxies
                        String ip = request.getHeader("X-Forwarded-For");
                        ctx.setIpAddress(ip != null ? ip.split(",")[0].trim() : request.getRemoteAddr());

                        // User agent
                        ctx.setUserAgent(request.getHeader("User-Agent"));

                    } catch (Exception e) {
                        // Never let audit context failure break authentication
                        logger.warn("Could not populate RequestContext: {}", e);
                    }
                    
                } else {
                    System.out.println(">>> Token invalid!");
                }
            } catch (Exception e) {
                System.out.println(">>> Token exception: " + e.getMessage());
            }
        } else {
            System.out.println(">>> No Bearer token found");
        }

        filterChain.doFilter(request, response);
    }
}