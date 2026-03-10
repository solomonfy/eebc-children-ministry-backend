# Spring Boot Auth Example (bcrypt + JWT)

This file shows a minimal example of server-side registration and login using Spring Boot, `BCryptPasswordEncoder` for hashing, and returning a JWT on successful login. Adjust to your project's package names and security setup.

## User entity

```java
@Entity
public class AppUser {
  @Id @GeneratedValue private Long id;
  private String email;
  private String passwordHash;
  private String role; // e.g. ADMIN, STAFF, VOLUNTEER

  // getters/setters
}
```

## DTOs

```java
public record RegisterRequest(String email, String password, String firstName, String lastName, String role) {}
public record LoginRequest(String email, String password) {}
public record AuthResponse(String token) {}
```

## Repository

```java
public interface UserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByEmail(String email);
}
```

## Service (hash + verify)

```java
@Service
public class AuthService {
  private final UserRepository users;
  private final BCryptPasswordEncoder encoder;
  // jwt util omitted for brevity

  public AuthService(UserRepository users, BCryptPasswordEncoder encoder) {
    this.users = users;
    this.encoder = encoder;
  }

  public void register(RegisterRequest req) {
    if (users.findByEmail(req.email()).isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email exists");
    String hash = encoder.encode(req.password());
    AppUser u = new AppUser();
    u.setEmail(req.email());
    u.setPasswordHash(hash);
    u.setRole(req.role());
    users.save(u);
  }

  public String login(LoginRequest req) {
    AppUser u = users.findByEmail(req.email()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    if (!encoder.matches(req.password(), u.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    // create and return a JWT (implementation depends on your library)
    return "<jwt-for-user>";
  }
}
```

## Controller

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService auth;

  public AuthController(AuthService auth) { this.auth = auth; }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    // validate input, enforce password policy
    auth.register(req);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
    String token = auth.login(req);
    return ResponseEntity.ok(new AuthResponse(token));
  }
}
```

## Configuration

```java
@Configuration
public class SecurityConfig {
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }
}
```

## Notes / Best practices

- Never store plaintext passwords; only store the bcrypt hash.
- Do not log raw passwords or include them in error messages.
- Always serve API over HTTPS in production.
- Validate and enforce password strength server-side (in addition to frontend checks).
- Prefer HttpOnly secure cookies for tokens if you want to avoid `localStorage` XSS exposure; otherwise issue JWTs and use short expiry + refresh tokens.

This example is intentionally minimal—integrate with your JWT library (e.g., `jjwt` or `spring-security-oauth`) and your application's user model.
