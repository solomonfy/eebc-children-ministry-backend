package com.eebc.childrenministry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildDTO {

    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private List<ChildAllergyDTO> allergies;

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
    }
}
