package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rooms")
//@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "campus_id", nullable = false)
    private String campus_id;

    // ← NEW: which classroom this room belongs to
    @Column(name = "classroom_id", nullable = false)
    private String classroomId;

    @ManyToOne
    @JoinColumn(name = "ministry_id", nullable = false)
    private Ministry ministry_id;

    private String notes;
    private Integer capacity;
    private String location;
    private Long active = 1L;

    @Column(name="min_age_months")
    private Integer minAgeMonths;

    @Column(name="max_age_months")
    private Integer maxAgeMonths;

    @Column(name="ratio_child_to_volunteer")
    private Integer ratioChildToVolunteer;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    private String status = "ACTIVE";

    public void setMinistry_id(String ministryId) {
        this.ministry_id = new Ministry();
        this.ministry_id.setId(ministryId);
    }
}
