package com.eebc.childrenministry.entity;

import com.eebc.childrenministry.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.sql.Time;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "services")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServiceEvent extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String name;
    private LocalDateTime scheduled_date;
    private Time start_time;
    private Time end_time;
    private Long expected_attendance = 1L;
    private String notes;
    private String campus_id;
    private String ministry_id;
    private String service_type;
    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();
}
