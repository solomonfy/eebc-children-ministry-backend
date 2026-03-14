package com.eebc.childrenministry.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "children")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Child extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "family_id", nullable = false, columnDefinition = "CHAR(36)")
    private String familyId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String nickname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String gender;

    private String grade;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "default_room_id", columnDefinition = "CHAR(36)")
    private String defaultRoomId;

    @Column(name = "special_needs")
    private String specialNeeds;

    @Column(name = "epi_pen_required")
    private Boolean epiPenRequired = false;

    private String notes;

    @Column(name = "medical_conditions", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> medicalConditions = new ArrayList<>();

    @Column(name = "medications", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> medications = new ArrayList<>();

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ChildAllergy> allergies = new ArrayList<>();

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(name = "enrolled_date")
    private LocalDate enrolledDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}