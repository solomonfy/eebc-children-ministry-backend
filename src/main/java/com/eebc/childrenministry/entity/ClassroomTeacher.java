package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// ══════════════════════════════════════════════
// ClassroomTeacher.java
// Join table: assigns a TEACHER (user) to a
// classroom, with an optional default room
// within that classroom.
//
// One teacher can only appear once per classroom
// (enforced by unique constraint in DB).
// ══════════════════════════════════════════════
@Entity
@Table(
    name = "classroom_teachers",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_classroom_teacher",
        columnNames = {"classroom_id", "user_id"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "classroom_id", nullable = false)
    private String classroomId;

    // Must be a user with role = TEACHER
    @Column(name = "user_id", nullable = false)
    private String userId;

    // Default room within this classroom for this teacher
    // Room must belong to the same classroom
    @Column(name = "default_room_id")
    private String defaultRoomId;

    // true = lead teacher, false = assistant teacher
    @Column(name = "is_lead", nullable = false,
            columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isLead = false;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    private String status = "ACTIVE";

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
