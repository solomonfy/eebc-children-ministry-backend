package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "classrooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    // e.g. Nursery, Toddlers, 3 Year, Pre-K
    @Column(nullable = false)
    private String name;

    private String description;

    // Human-readable age group label e.g. "0-12 months"
    @Column(name = "age_group")
    private String ageGroup;

    // Age range in months for automatic child-to-classroom matching
    @Column(name = "min_age_months")
    private Integer minAgeMonths;

    @Column(name = "max_age_months")
    private Integer maxAgeMonths;

    // Assigned coordinator — users.id with role CLASS_COORDINATOR
    @Column(name = "coordinator_id")
    private String coordinatorId;

    // Assigned assistant — users.id with role CLASS_ASSISTANT
    @Column(name = "assistant_id")
    private String assistantId;

    @Column(name = "campus_id")
    private String campusId;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
