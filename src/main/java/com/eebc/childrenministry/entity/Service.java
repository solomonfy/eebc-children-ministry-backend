package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "services",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_service_date_type_campus_status",
                columnNames = {"service_date", "type", "campus_id", "status"}
        )
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Service extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // FRIDAY_EVENING | SUNDAY_MORNING
    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'SCHEDULED'")
    private String status = "SCHEDULED";

    @Column(name = "campus_id", nullable = false)
    private String campusId;

    @Column(name = "ministry_id", nullable = false)
    private String ministryId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // These already exist in your table — add them to the entity:
    private Integer expectedAttendance;
    private String notes;
}