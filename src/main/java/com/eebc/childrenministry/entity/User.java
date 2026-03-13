package com.eebc.childrenministry.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User  extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    @JsonIgnore
    private String passwordHash;

    @Column(name = "user_name", unique = true)
    private String userName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String phone;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'volunteer'")
    private String role;

    private String status;

    /** true = receive email notifications (default true) */
    @Column(name = "notify_email", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean notifyEmail = true;

    /** true = receive SMS notifications (default true) */
    @Column(name = "notify_sms", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean notifySms = true;

    @CreationTimestamp
    private LocalDateTime created_at = LocalDateTime.now();

    @UpdateTimestamp
    private LocalDateTime updated_at = LocalDateTime.now();
}
