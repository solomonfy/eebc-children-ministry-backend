package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private String notes;
    private String capacity;
    private String location;
    private Long active = 1L;
    private Long min_age_months = 0L;
    private Long max_age_months = 0L;
    private Long ratio_child_to_volunteer = 0L;

    @Column(nullable = false)
    private String campus_id;

//    @Column(nullable = false)
//    private String ministry_id;

    @ManyToOne
    @JoinColumn(name = "ministry_id", nullable = false)
    private Ministry ministry_id;

    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();
}
