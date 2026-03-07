package com.eebc.childrenministry.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildDTO {

    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String defaultRoomId;
    private String familyId;
    private String nickname;
    private String gender;
    private String grade;
    private String photoUrl;
    private String notes;
    private String specialNeeds;
    private Boolean epiPenRequired = false;
    private List<ChildAllergyDTO> allergies;
    private List<String> medicalConditions = new ArrayList<>();
    private List<String> medications = new ArrayList<>();
    private String status = "ACTIVE";
    private LocalDate enrolledDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChildAllergyDTO {
        private String id;
        private String allergyName;
        private String severity;
        private String notes;
        private String reaction;
        private String treatment;
//        private Child child;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
