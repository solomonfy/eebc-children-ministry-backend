package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    // Which entity type changed — "User", "Classroom", "Room", "Guardian", etc.
    @Column(name = "entity_name", nullable = false, length = 100)
    private String entityName;

    // PK of the changed record
    @Column(name = "entity_id", nullable = false, length = 100)
    private String entityId;

    // INSERT | UPDATE | DELETE
    @Column(nullable = false, length = 10)
    private String action;

    // Human-readable description e.g. "Teacher assigned", "Status updated", "Added as new"
    @Column(columnDefinition = "TEXT")
    private String description;

    // userId from JWT — null means system/cron action
    @Column(name = "changed_by", length = 100)
    private String changedBy;

    // Display name captured at time of change (denormalized for history stability)
    @Column(name = "changed_by_name", length = 200)
    private String changedByName;

    // Full JSON snapshot before the change (null for INSERT)
    @Column(name = "old_value", columnDefinition = "JSON")
    private String oldValue;

    // Full JSON snapshot after the change (null for DELETE)
    @Column(name = "new_value", columnDefinition = "JSON")
    private String newValue;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    private LocalDateTime changedAt;
}
