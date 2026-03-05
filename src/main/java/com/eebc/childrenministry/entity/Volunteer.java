package com.eebc.childrenministry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "volunteers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name="first_name", nullable = false)
    private String firstName;

    @Column(name= "last_name", nullable = false)
    private String lastName;

    private String email;
    private String phone;
    private String notes;
    private String photo_url;

    @Column(name = "background_check_status")
    private String background_check_status;

    @Column(name = "background_check_date")
    private LocalDateTime background_check_date;

    @Column(nullable = false)
    private UUID campus_id;

    private Boolean active = true;

    private String user_id;

    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();
}
