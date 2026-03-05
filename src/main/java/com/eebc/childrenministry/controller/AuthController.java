package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.AuthResponse;
import com.eebc.childrenministry.dto.LoginRequest;
import com.eebc.childrenministry.dto.RegisterRequest;
import com.eebc.childrenministry.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

//    private final AuthService auth;
//    public AuthController(AuthService auth) {
//        this.auth = auth;
//    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        // ideally validate input (email format, password strength)
        authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String token = authService.login(req);
        return ResponseEntity.ok(Map.of("token", token));  // ← returns { "token": "eyJ..." }
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
//        String token = authService.login(req);
//        return ResponseEntity.ok(token);  // ← returns plain string, not JSON
//    }

    //    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
//        String token = auth.login(req);
//        return ResponseEntity.ok(new AuthResponse(token));
//    }

//    @PostMapping("/login")
//    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest req) {
//        String token = authService.login(req);
//        return ResponseEntity.ok(Map.of("token", token));
//    }
}
