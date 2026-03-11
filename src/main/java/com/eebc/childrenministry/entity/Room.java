package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Room extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    // Plain String FK — same pattern as campus_id
    @Column(name = "campus_id", nullable = false)
    private String campusId;

    // ← classroom this room belongs to (optional — room may not be in a classroom yet)
    @Column(name = "classroom_id")
    private String classroomId;

    // Changed from @ManyToOne to plain String FK to avoid detached entity issues
    // Ministry is looked up by ID on the frontend — no need for a full JPA join here
    @Column(name = "ministry_id", nullable = false)
    private String ministryId;

    private String notes;
    private Integer capacity;
    private String location;
    private Long active = 1L;

    @Column(name = "min_age_months")
    private Integer minAgeMonths;

    @Column(name = "max_age_months")
    private Integer maxAgeMonths;

    @Column(name = "ratio_child_to_volunteer")
    private Integer ratioChildToVolunteer;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}