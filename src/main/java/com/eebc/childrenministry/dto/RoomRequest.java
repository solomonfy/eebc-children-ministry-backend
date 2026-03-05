package com.eebc.childrenministry.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequest {
    private String name;
    private Integer capacity;
    private String location;
    private Integer min_age_months;
    private Integer max_age_months;
    private Long campus_id;
    private Long ministry_id; // ✅ accepts plain number from frontend
}
