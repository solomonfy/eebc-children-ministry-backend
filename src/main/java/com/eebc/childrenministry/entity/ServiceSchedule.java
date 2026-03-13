package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * One row per assignment: which teacher is in which classroom/room for a given service.
 */
@Entity
@Table(name = "service_schedules",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_schedule_service_classroom_teacher",
        columnNames = {"service_id", "classroom_id", "teacher_id"}
    )
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServiceSchedule extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "service_id", nullable = false)
    private String serviceId;

    @Column(name = "classroom_id", nullable = false)
    private String classroomId;

    // nullable — room may be assigned later
    @Column(name = "room_id")
    private String roomId;

    @Column(name = "teacher_id", nullable = false)
    private String teacherId;

    // LEAD | ASSISTANT
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ASSISTANT'")
    private String role = "ASSISTANT";

    // SCHEDULED | CONFIRMED | DECLINED | COMPLETED
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'SCHEDULED'")
    private String status = "SCHEDULED";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
