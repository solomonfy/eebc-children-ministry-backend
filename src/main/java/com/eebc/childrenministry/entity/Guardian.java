package com.eebc.childrenministry.entity;

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
@Table(name = "guardians")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guardian extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "family_id", nullable = false)
    private String familyId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String phone;
    private String email;

    @Column(nullable = false, columnDefinition = "VARCHAR(65) DEFAULT 'PARENT'")
    private String relationship = "PARENT";

    @Column(name = "is_primary", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isPrimary = false;


    @Column(name = "allowed_pickup", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean allowedPickup = true;

    // ── Kiosk PIN ───────────────────────────
    // Raw PIN never stored — only BCrypt hash
    // NULL means PIN not set yet for this guardian
    @Column(name = "checkin_pin_hash", nullable = true)
    private String checkinPinHash;

    @Column(name = "pin_set_at")
    private LocalDateTime pinSetAt;

    @Column(name = "campus_id", columnDefinition = "VARCHAR(36)")
    private String campusId;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(nullable = false,
            columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
