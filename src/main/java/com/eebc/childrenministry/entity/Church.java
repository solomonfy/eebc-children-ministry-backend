package com.eebc.childrenministry.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "churches")
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Church extends Auditable {

    @Id
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    private String address_street;
    private String address_city;
    private String address_state;
    private String address_zip;
    private String phone;
    private String email;
    private String website;

    @Column(columnDefinition = "varchar(50) default 'America/Chicago'")
    private String timezone = "America/Chicago";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}