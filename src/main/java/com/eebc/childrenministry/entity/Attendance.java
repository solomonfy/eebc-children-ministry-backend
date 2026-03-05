package com.eebc.childrenministry.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID child_id;
    private UUID family_id;
    private UUID service_id;
    private UUID room_id;
    private LocalDateTime check_in_time;
    private String check_in_method;
    private String pickup_code;
    private String tag_code;
    private String status;
    private Boolean label_printed = false;
}
