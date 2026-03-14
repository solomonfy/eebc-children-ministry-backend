package com.eebc.childrenministry.service;

import com.eebc.childrenministry.dto.LoginRequest;
import com.eebc.childrenministry.dto.RegisterRequest;
import com.eebc.childrenministry.entity.User;
import com.eebc.childrenministry.enums.Status;
import com.eebc.childrenministry.repository.UserRepository;
import com.eebc.childrenministry.util.JwtUtil;
import com.eebc.childrenministry.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    ValidationUtil validator;


    public AuthService(UserRepository userRepository, BCryptPasswordEncoder encoder, JwtUtil jwtUtil, ValidationUtil validator) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.validator = validator;
    }

    public void register(RegisterRequest req) {
        String emailErr = validator.validateEmail(req.email());
        if (emailErr != null) {
            logger.error("Registration failed for email {}: {}", req.email(), emailErr);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, emailErr);
        }
        String passwordErr = validator.validatePassword(req.password());
        if (passwordErr != null) {
            logger.error("Registration failed for email {}: {}", req.email(), passwordErr);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, passwordErr);
        }
        String roleErr = validator.validateRole(req.role());
        if (roleErr != null) {
            logger.error("Registration failed for email {}: {}", req.email(), roleErr);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, roleErr);
        }
        if (userRepository.findByEmail(req.email()).isPresent()) {
            logger.error("Registration failed for email {}: Email already exists", req.email());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        String hash = encoder.encode(req.password());
        User u = new User();
        u.setFirstName(req.firstName());
        u.setLastName(req.lastName());
        u.setStatus(Status.ACTIVE.name());
        u.setEmail(req.email());
        u.setPasswordHash(hash);
        u.setRole(req.role().toUpperCase());
        u.setCampusId(req.campusId());
        u.setChurchId(req.churchId());
        u.setUserName(generateUserName(req));
        logger.info("Registering new user with email {}", req.email());
        userRepository.save(u);
    }

    public String login(LoginRequest req) {
        logger.info("Login attempt for email {}", req.email());
        User u = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            logger.error("Login failed for email {}: Invalid credentials", req.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        logger.info("User {} logged in successfully", req.email());
        return jwtUtil.generateToken(u.getEmail(), u.getRole(), u.getId(),
                u.getFirstName(), u.getLastName(), u.getUserName(),
                u.getCampusId(), u.getChurchId());
    }

    // 2. Generate username
    private String generateUserName(RegisterRequest req) {
        String baseUsername = (String.valueOf(req.firstName().charAt(0)) + req.lastName()).toLowerCase();
        String username = baseUsername + "001";
        if (userRepository.findByUserName(username).isPresent()) {
            int suffix = 2;
            while (userRepository.findByUserName(baseUsername + String.format("%03d", suffix)).isPresent()) {
                suffix++;
            }
            username = baseUsername + String.format("%03d", suffix);
        }
        return username;
    }
}
