package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "attendance")
public class Attendance extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "child_id", nullable = false)
    private String childId;

    @Column(name = "family_id", nullable = false)
    private String familyId;

    @Column(name = "service_id", nullable = false)
    private String serviceId;

    @Column(name = "room_id")
    private String roomId;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_in_method", length = 65)
    private String checkInMethod = "KIOSK";

    @Column(name = "check_in_guardian_id")
    private String checkInGuardianId;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "check_out_guardian_id")
    private String checkOutGuardianId;

    @Column(name = "room_verified_time")
    private LocalDateTime roomVerifiedTime;

    @Column(name = "verified_by_volunteer_id")
    private String verifiedByVolunteerId;

    @Column(name = "pickup_code")
    private String pickupCode;

    @Column(name = "tag_code")
    private String tagCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 65, nullable = false)
    private AttendanceStatus status;

    @Column(name = "label_printed")
    private Boolean labelPrinted = false;

    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AttendanceStatus {
        PENDING_AT_ROOM,
        PRESENT,
        CHECKED_IN,
        CHECKED_OUT,
        ABSENT,
        CANCELLED
    }
}
