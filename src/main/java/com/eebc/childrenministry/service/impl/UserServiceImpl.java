package com.eebc.childrenministry.service.impl;

import com.eebc.childrenministry.entity.User;
import com.eebc.childrenministry.dto.CreateUserRequest;
import com.eebc.childrenministry.repository.UserRepository;
import com.eebc.childrenministry.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Read ───────────────────────────────────

    @Override
    public List<User> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            logger.info("Fetched all users, count: {}", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error fetching all users: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<User> getUsersByRole(String role) {
        try {
            List<User> users = userRepository.findByRole(role);
            logger.info("Fetched users by role={}, count: {}", role, users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error fetching users by role {}: {}", role, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<User> getUsersByStatus(String status) {
        try {
            List<User> users = userRepository.findByStatus(status);
            logger.info("Fetched users by status={}, count: {}", status, users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error fetching users by status {}: {}", status, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<User> getUsersByRoleAndStatus(String role, String status) {
        try {
            List<User> users = userRepository.findByRoleAndStatus(role, status);
            logger.info("Fetched users by role={} status={}, count: {}", role, status, users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error fetching users by role+status: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public User getUserById(String id) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) logger.warn("User not found with id={}", id);
            else logger.info("Fetched user id={}", id);
            return user;
        } catch (Exception e) {
            logger.error("Error fetching user by id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email).orElse(null);
            logger.info("Fetched user by email={}: {}", email, user != null ? "found" : "not found");
            return user;
        } catch (Exception e) {
            logger.error("Error fetching user by email {}: {}", email, e.getMessage());
            throw e;
        }
    }

    @Override
    public User getUserByPhone(String phone) {
        try {
            User user = userRepository.findByPhone(phone).orElse(null);
            logger.info("Fetched user by phone: {}", user != null ? "found" : "not found");
            return user;
        } catch (Exception e) {
            logger.error("Error fetching user by phone: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public User getUserByUserName(String userName) {
        try {
            User user = userRepository.findByUserName(userName).orElse(null);
            logger.info("Fetched user by userName={}: {}", userName, user != null ? "found" : "not found");
            return user;
        } catch (Exception e) {
            logger.error("Error fetching user by userName {}: {}", userName, e.getMessage());
            throw e;
        }
    }

    @Override
    public User getUserByEmailOrPhone(String email, String phone) {
        try {
            User user = userRepository.findByEmailOrPhone(email, phone).orElse(null);
            logger.info("Fetched user by email/phone: {}", user != null ? "found" : "not found");
            return user;
        } catch (Exception e) {
            logger.error("Error fetching user by email/phone: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public User getUserByEmailOrUserName(String email, String userName) {
        try {
            User user = userRepository.findByEmailOrUserName(email, userName).orElse(null);
            logger.info("Fetched user by email/userName: {}", user != null ? "found" : "not found");
            return user;
        } catch (Exception e) {
            logger.error("Error fetching user by email/userName: {}", e.getMessage());
            throw e;
        }
    }


    // ── Username generation ────────────────────
    // Produces kim.john001, kim.john002, etc.
    // Strips non-alphanumeric chars, finds all existing users with that base,
    // then picks the next available 3-digit suffix.
    private String generateUserName(String firstName, String lastName) {
        String base = firstName.toLowerCase().replaceAll("[^a-z0-9]", "")
                + "."
                + lastName.toLowerCase().replaceAll("[^a-z0-9]", "");

        List<User> existing = userRepository.findByUserNameStartingWith(base);

        int suffix = 1;
        while (suffix <= 999) {
            String candidate = base + String.format("%03d", suffix);
            boolean taken = existing.stream()
                    .anyMatch(u -> u.getUserName().equals(candidate));
            if (!taken) return candidate;
            suffix++;
        }
        // Extremely unlikely fallback — append timestamp
        return base + System.currentTimeMillis();
    }

    // ── Preferred: create from request DTO (plain password from frontend) ──
    @Override
    public User createUser(CreateUserRequest req) {
        try {
            if (req.password() == null || req.password().isBlank())
                throw new IllegalArgumentException("Password is required");
            if (req.email() == null || req.email().isBlank())
                throw new IllegalArgumentException("Email is required");
            if (req.firstName() == null || req.firstName().isBlank())
                throw new IllegalArgumentException("First name is required");
            if (req.lastName() == null || req.lastName().isBlank())
                throw new IllegalArgumentException("Last name is required");

            if (userRepository.existsByEmail(req.email()))
                throw new IllegalArgumentException("Email already in use: " + req.email());
            if (req.phone() != null && userRepository.existsByPhone(req.phone()))
                throw new IllegalArgumentException("Phone already in use: " + req.phone());

            User user = new User();
            user.setFirstName(req.firstName());
            user.setLastName(req.lastName());
            user.setEmail(req.email());

            // If userName explicitly provided, validate it; otherwise auto-generate kim.john001
            if (req.userName() != null && !req.userName().isBlank()) {
                if (userRepository.existsByUserName(req.userName()))
                    throw new IllegalArgumentException("Username already in use: " + req.userName());
                user.setUserName(req.userName());
            } else {
                user.setUserName(generateUserName(req.firstName(), req.lastName()));
            }
            user.setPasswordHash(passwordEncoder.encode(req.password())); // ← hash here
            user.setPhone(req.phone());
            user.setPhotoUrl(req.photoUrl());
            user.setRole(req.role() != null ? req.role() : "TEACHER");
            user.setStatus(req.status() != null ? req.status() : "ACTIVE");

            User saved = userRepository.save(user);
            logger.info("Created user id={}, email={}", saved.getId(), saved.getEmail());
            return saved;
        } catch (Exception e) {
            logger.error("Error creating user from request: {}", e.getMessage());
            throw e;
        }
    }

//    @Override
//    public User createUser(User user) {
//        try {
//            if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail()))
//                throw new IllegalArgumentException("Email already in use: " + user.getEmail());
//            if (user.getUserName() != null && userRepository.existsByUserName(user.getUserName()))
//                throw new IllegalArgumentException("Username already in use: " + user.getUserName());
//            if (user.getPhone() != null && userRepository.existsByPhone(user.getPhone()))
//                throw new IllegalArgumentException("Phone already in use: " + user.getPhone());
//
//            // Hash plain-text password before persisting
//            if (user.getPasswordHash() != null && !user.getPasswordHash().isBlank())
//                user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
//
//            if (user.getStatus() == null) user.setStatus("ACTIVE");
//
//            User saved = userRepository.save(user);
//            logger.info("Created new user id={}, email={}", saved.getId(), saved.getEmail());
//            return saved;
//        } catch (Exception e) {
//            logger.error("Error creating user: {}", e.getMessage());
//            throw e;
//        }
//    }

    @Override
    public User updateUser(String id, User updates) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

            if (updates.getFirstName() != null) user.setFirstName(updates.getFirstName());
            if (updates.getLastName()  != null) user.setLastName(updates.getLastName());
            if (updates.getPhone()     != null) user.setPhone(updates.getPhone());
            if (updates.getPhotoUrl()  != null) user.setPhotoUrl(updates.getPhotoUrl());
            if (updates.getRole()      != null) user.setRole(updates.getRole());
            if (updates.getStatus()    != null) user.setStatus(updates.getStatus());

            // Email — check uniqueness only if changed
            if (updates.getEmail() != null && !updates.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(updates.getEmail()))
                    throw new IllegalArgumentException("Email already in use: " + updates.getEmail());
                user.setEmail(updates.getEmail());
            }

            // Username — check uniqueness only if changed
            if (updates.getUserName() != null && !updates.getUserName().equals(user.getUserName())) {
                if (userRepository.existsByUserName(updates.getUserName()))
                    throw new IllegalArgumentException("Username already in use: " + updates.getUserName());
                user.setUserName(updates.getUserName());
            }

            // Notification preferences
            if (updates.getNotifyEmail() != null) user.setNotifyEmail(updates.getNotifyEmail());
            if (updates.getNotifySms()   != null) user.setNotifySms(updates.getNotifySms());

            // Password — only update if a new one is explicitly provided
            if (updates.getPasswordHash() != null && !updates.getPasswordHash().isBlank())
                user.setPasswordHash(passwordEncoder.encode(updates.getPasswordHash()));

            User saved = userRepository.save(user);
            logger.info("Updated user id={}", id);
            return saved;
        } catch (Exception e) {
            logger.error("Error updating user id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteUser(String id) {
        try {
            if (!userRepository.existsById(id))
                throw new IllegalArgumentException("User not found: " + id);
            userRepository.deleteById(id);
            logger.info("Deleted user id={}", id);
        } catch (Exception e) {
            logger.error("Error deleting user id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ── Existence checks ───────────────────────

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
}