package com.eebc.childrenministry.controller;

import com.eebc.childrenministry.dto.CreateUserRequest;
import com.eebc.childrenministry.dto.UserResponse;
import com.eebc.childrenministry.entity.User;
import com.eebc.childrenministry.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── Helper: entity → safe DTO ─────────────
    // password_hash is NEVER included in any response
    private UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                u.getUserName(),
                u.getPhone(),
                u.getPhotoUrl(),
                u.getRole(),
                u.getStatus()
        );
    }

    private List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── GET /users
    // ── GET /users?role=TEACHER
    // ── GET /users?status=ACTIVE
    // ── GET /users?role=TEACHER&status=ACTIVE
    @GetMapping
    public ResponseEntity<List<UserResponse>> list(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        List<User> users;
        if (role != null && status != null)
            users = userService.getUsersByRoleAndStatus(role, status);
        else if (role != null)
            users = userService.getUsersByRole(role);
        else if (status != null)
            users = userService.getUsersByStatus(status);
        else
            users = userService.getAllUsers();
        return ResponseEntity.ok(toResponseList(users));
    }

    // ── GET /users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable String id) {
        User user = userService.getUserById(id);
        return user != null
                ? ResponseEntity.ok(toResponse(user))
                : ResponseEntity.notFound().build();
    }

    // ── GET /users/by-email?email=...
    @GetMapping("/by-email")
    public ResponseEntity<UserResponse> getByEmail(@RequestParam String email) {
        User user = userService.getUserByEmail(email);
        return user != null
                ? ResponseEntity.ok(toResponse(user))
                : ResponseEntity.notFound().build();
    }

    // ── GET /users/by-username?userName=...
    @GetMapping("/by-username")
    public ResponseEntity<UserResponse> getByUserName(@RequestParam String userName) {
        User user = userService.getUserByUserName(userName);
        return user != null
                ? ResponseEntity.ok(toResponse(user))
                : ResponseEntity.notFound().build();
    }

    // ── GET /users/by-phone?phone=...
    @GetMapping("/by-phone")
    public ResponseEntity<UserResponse> getByPhone(@RequestParam String phone) {
        User user = userService.getUserByPhone(phone);
        return user != null
                ? ResponseEntity.ok(toResponse(user))
                : ResponseEntity.notFound().build();
    }

    // ── GET /users/exists?email=...
    // ── GET /users/exists?userName=...
    // ── GET /users/exists?phone=...
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> exists(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phone) {
        boolean exists = false;
        if (email         != null) exists = userService.existsByEmail(email);
        else if (userName != null) exists = userService.existsByUserName(userName);
        else if (phone    != null) exists = userService.existsByPhone(phone);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // ── POST /users
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUserRequest request) {
        try {
            User created = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ── PUT /users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody User updates) {
        try {
            User updated = userService.updateUser(id, updates);
            return ResponseEntity.ok(toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE /users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}