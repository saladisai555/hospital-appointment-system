package com.example.hospital_appointment_system.dto.response;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentResponse {
    private Integer id;
    private String name;
    private String description;
    private boolean active;
}