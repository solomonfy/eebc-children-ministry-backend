package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_type_config",
    uniqueConstraints = @UniqueConstraint(columnNames = {"ministry_id", "code"}))
@NoArgsConstructor
@Getter
@Setter
public class ServiceTypeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "ministry_id", nullable = false)
    private String ministryId;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 60)
    private String label;

    /** Color key mapped to Tailwind classes in the frontend (e.g. "violet", "amber"). */
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'blue'")
    private String color = "blue";

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean active = true;

    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0")
    private int sortOrder = 0;
}
