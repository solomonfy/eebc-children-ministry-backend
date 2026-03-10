package com.eebc.childrenministry.entity;

import com.eebc.childrenministry.enums.CheckInMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ministry_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinistrySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "ministry_id", nullable = false, unique = true)
    private String ministryId;

    // ── Check-in Mode ──────────────────────────
    // PRINT_ONLY | DIGITAL_ONLY | PRINT_AND_DIGITAL
    @Column(name = "checkin_mode", nullable = false)
    private String checkinMode = CheckInMode.PRINT_ONLY.name();

    // ── Check-in Methods ───────────────────────
    @Column(name = "enable_last_name_checkin")
    private Boolean enableLastNameCheckin = true;

    @Column(name = "enable_phone_number_checkin")
    private Boolean enablePhoneNumberCheckin = true;

    @Column(name = "enable_qr_code_checkin")
    private Boolean enableQrCodeCheckin = true;

    @Column(name = "enable_pin_code_checkin")
    private Boolean enablePinCodeCheckin = true;

    // ── Timing ─────────────────────────────────
    @Column(name = "checkin_early_minutes")
    private Integer checkinEarlyMinutes = 30;

    @Column(name = "checkin_late_minutes")
    private Integer checkinLateMinutes = 15;

    // ── Auto Checkout ──────────────────────────
    @Column(name = "auto_checkout_enabled")
    private Boolean autoCheckoutEnabled = false;

    @Column(name = "auto_checkout_minutes")
    private Integer autoCheckoutMinutes = 30;

    // ── Pickup ─────────────────────────────────
    @Column(name = "pickup_code_length")
    private Integer pickupCodeLength = 4;

    @Column(name = "require_pickup_code")
    private Boolean requirePickupCode = true;

    @Column(name = "allow_guest_checkin")
    private Boolean allowGuestCheckin = true;

    // ── Features ───────────────────────────────
    @Column(name = "lessons_enabled")
    private Boolean lessonsEnabled = true;

    @Column(name = "parent_recaps_enabled")
    private Boolean parentRecapsEnabled = true;

    @Column(name = "incidents_enabled")
    private Boolean incidentsEnabled = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
